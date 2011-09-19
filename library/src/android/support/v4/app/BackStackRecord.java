/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v4.app;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.actionbarsherlock.R;

final class BackStackState implements Parcelable {
    final int[] mOps;
    final int mTransition;
    final int mTransitionStyle;
    final String mName;
    final int mIndex;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;

    public BackStackState(FragmentManagerImpl fm, BackStackRecord bse) {
        int numRemoved = 0;
        BackStackRecord.Op op = bse.mHead;
        while (op != null) {
            if (op.removed != null) numRemoved += op.removed.size();
            op = op.next;
        }
        mOps = new int[bse.mNumOp*5 + numRemoved];

        if (!bse.mAddToBackStack) {
            throw new IllegalStateException("Not on back stack");
        }

        op = bse.mHead;
        int pos = 0;
        while (op != null) {
            mOps[pos++] = op.cmd;
            mOps[pos++] = op.fragment.mIndex;
            mOps[pos++] = op.enterAnim;
            mOps[pos++] = op.exitAnim;
            if (op.removed != null) {
                final int N = op.removed.size();
                mOps[pos++] = N;
                for (int i=0; i<N; i++) {
                    mOps[pos++] = op.removed.get(i).mIndex;
                }
            } else {
                mOps[pos++] = 0;
            }
            op = op.next;
        }
        mTransition = bse.mTransition;
        mTransitionStyle = bse.mTransitionStyle;
        mName = bse.mName;
        mIndex = bse.mIndex;
        mBreadCrumbTitleRes = bse.mBreadCrumbTitleRes;
        mBreadCrumbTitleText = bse.mBreadCrumbTitleText;
        mBreadCrumbShortTitleRes = bse.mBreadCrumbShortTitleRes;
        mBreadCrumbShortTitleText = bse.mBreadCrumbShortTitleText;
    }

    public BackStackState(Parcel in) {
        mOps = in.createIntArray();
        mTransition = in.readInt();
        mTransitionStyle = in.readInt();
        mName = in.readString();
        mIndex = in.readInt();
        mBreadCrumbTitleRes = in.readInt();
        mBreadCrumbTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        mBreadCrumbShortTitleRes = in.readInt();
        mBreadCrumbShortTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    public BackStackRecord instantiate(FragmentManagerImpl fm) {
        BackStackRecord bse = new BackStackRecord(fm);
        int pos = 0;
        while (pos < mOps.length) {
            BackStackRecord.Op op = new BackStackRecord.Op();
            op.cmd = mOps[pos++];
            if (FragmentManagerImpl.DEBUG) Log.v(FragmentManagerImpl.TAG,
                    "BSE " + bse + " set base fragment #" + mOps[pos]);
            Fragment f = fm.mActive.get(mOps[pos++]);
            op.fragment = f;
            op.enterAnim = mOps[pos++];
            op.exitAnim = mOps[pos++];
            final int N = mOps[pos++];
            if (N > 0) {
                op.removed = new ArrayList<Fragment>(N);
                for (int i=0; i<N; i++) {
                    if (FragmentManagerImpl.DEBUG) Log.v(FragmentManagerImpl.TAG,
                            "BSE " + bse + " set remove fragment #" + mOps[pos]);
                    Fragment r = fm.mActive.get(mOps[pos++]);
                    op.removed.add(r);
                }
            }
            bse.addOp(op);
        }
        bse.mTransition = mTransition;
        bse.mTransitionStyle = mTransitionStyle;
        bse.mName = mName;
        bse.mIndex = mIndex;
        bse.mAddToBackStack = true;
        bse.mBreadCrumbTitleRes = mBreadCrumbTitleRes;
        bse.mBreadCrumbTitleText = mBreadCrumbTitleText;
        bse.mBreadCrumbShortTitleRes = mBreadCrumbShortTitleRes;
        bse.mBreadCrumbShortTitleText = mBreadCrumbShortTitleText;
        bse.bumpBackStackNesting(1);
        return bse;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(mOps);
        dest.writeInt(mTransition);
        dest.writeInt(mTransitionStyle);
        dest.writeString(mName);
        dest.writeInt(mIndex);
        dest.writeInt(mBreadCrumbTitleRes);
        TextUtils.writeToParcel(mBreadCrumbTitleText, dest, 0);
        dest.writeInt(mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(mBreadCrumbShortTitleText, dest, 0);
    }

    public static final Parcelable.Creator<BackStackState> CREATOR
            = new Parcelable.Creator<BackStackState>() {
        public BackStackState createFromParcel(Parcel in) {
            return new BackStackState(in);
        }

        public BackStackState[] newArray(int size) {
            return new BackStackState[size];
        }
    };
}

/**
 * @hide Entry of an operation on the fragment back stack.
 */
final class BackStackRecord extends FragmentTransaction implements
        FragmentManager.BackStackEntry, Runnable {
    static final String TAG = "BackStackEntry";

    private static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    final FragmentManagerImpl mManager;

    static final int OP_NULL = 0;
    static final int OP_ADD = 1;
    static final int OP_REPLACE = 2;
    static final int OP_REMOVE = 3;
    static final int OP_HIDE = 4;
    static final int OP_SHOW = 5;
    static final int OP_DETACH = 6;
    static final int OP_ATTACH = 7;

    static final class Op {
        Op next;
        Op prev;
        int cmd;
        Fragment fragment;
        int enterAnim;
        int exitAnim;
        ArrayList<Fragment> removed;
    }

    Op mHead;
    Op mTail;
    int mNumOp;
    int mEnterAnim;
    int mExitAnim;
    int mTransition;
    int mTransitionStyle;
    boolean mAddToBackStack;
    boolean mAllowAddToBackStack = true;
    String mName;
    boolean mCommitted;
    int mIndex;

    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix); writer.print("mName="); writer.print(mName);
                writer.print(" mIndex="); writer.print(mIndex);
                writer.print(" mCommitted="); writer.println(mCommitted);
        if (mTransition != FragmentTransaction.TRANSIT_NONE) {
            writer.print(prefix); writer.print("mTransition=#");
                    writer.print(Integer.toHexString(mTransition));
                    writer.print(" mTransitionStyle=#");
                    writer.println(Integer.toHexString(mTransitionStyle));
        }
        if (mEnterAnim != 0 || mExitAnim !=0) {
            writer.print(prefix); writer.print("mEnterAnim=#");
                    writer.print(Integer.toHexString(mEnterAnim));
                    writer.print(" mExitAnim=#");
                    writer.println(Integer.toHexString(mExitAnim));
        }
        if (mBreadCrumbTitleRes != 0 || mBreadCrumbTitleText != null) {
            writer.print(prefix); writer.print("mBreadCrumbTitleRes=#");
                    writer.print(Integer.toHexString(mBreadCrumbTitleRes));
                    writer.print(" mBreadCrumbTitleText=");
                    writer.println(mBreadCrumbTitleText);
        }
        if (mBreadCrumbShortTitleRes != 0 || mBreadCrumbShortTitleText != null) {
            writer.print(prefix); writer.print("mBreadCrumbShortTitleRes=#");
                    writer.print(Integer.toHexString(mBreadCrumbShortTitleRes));
                    writer.print(" mBreadCrumbShortTitleText=");
                    writer.println(mBreadCrumbShortTitleText);
        }

        if (mHead != null) {
            writer.print(prefix); writer.println("Operations:");
            String innerPrefix = prefix + "    ";
            Op op = mHead;
            int num = 0;
            while (op != null) {
                writer.print(prefix); writer.print("  Op #"); writer.print(num);
                        writer.println(":");
                writer.print(innerPrefix); writer.print("cmd="); writer.print(op.cmd);
                        writer.print(" fragment="); writer.println(op.fragment);
                if (op.enterAnim != 0 || op.exitAnim != 0) {
                    writer.print(prefix); writer.print("enterAnim="); writer.print(op.enterAnim);
                            writer.print(" exitAnim="); writer.println(op.exitAnim);
                }
                if (op.removed != null && op.removed.size() > 0) {
                    for (int i=0; i<op.removed.size(); i++) {
                        writer.print(innerPrefix);
                        if (op.removed.size() == 1) {
                            writer.print("Removed: ");
                        } else {
                            writer.println("Removed:");
                            writer.print(innerPrefix); writer.print("  #"); writer.print(num);
                                    writer.print(": ");
                        }
                        writer.println(op.removed.get(i));
                    }
                }
                op = op.next;
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl manager) {
        mManager = manager;
    }

    public int getId() {
        return mIndex;
    }

    public int getBreadCrumbTitleRes() {
        return mBreadCrumbTitleRes;
    }

    public int getBreadCrumbShortTitleRes() {
        return mBreadCrumbShortTitleRes;
    }

    public CharSequence getBreadCrumbTitle() {
        if (mBreadCrumbTitleRes != 0) {
            return mManager.mActivity.getText(mBreadCrumbTitleRes);
        }
        return mBreadCrumbTitleText;
    }

    public CharSequence getBreadCrumbShortTitle() {
        if (mBreadCrumbShortTitleRes != 0) {
            return mManager.mActivity.getText(mBreadCrumbShortTitleRes);
        }
        return mBreadCrumbShortTitleText;
    }

    void addOp(Op op) {
        if (mHead == null) {
            mHead = mTail = op;
        } else {
            op.prev = mTail;
            mTail.next = op;
            mTail = op;
        }
        op.enterAnim = mEnterAnim;
        op.exitAnim = mExitAnim;
        mNumOp++;
    }

    public FragmentTransaction add(Fragment fragment, String tag) {
        doAddOp(0, fragment, tag, OP_ADD);
        return this;
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        doAddOp(containerViewId, fragment, null, OP_ADD);
        return this;
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        doAddOp(containerViewId, fragment, tag, OP_ADD);
        return this;
    }

    private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd) {
        if (fragment.mImmediateActivity != null) {
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
        fragment.mImmediateActivity = mManager.mActivity;
        fragment.mFragmentManager = mManager;

        if (tag != null) {
            if (fragment.mTag != null && !tag.equals(fragment.mTag)) {
                throw new IllegalStateException("Can't change tag of fragment "
                        + fragment + ": was " + fragment.mTag
                        + " now " + tag);
            }
            fragment.mTag = tag;
        }

        if (containerViewId != 0) {
            //This will change the target container ID to be the content view
            //of our custom action bar implementation when the entire activity
            //view is selected as the target and we are pre-honeycomb
            if (!IS_HONEYCOMB && (containerViewId == android.R.id.content)) {
                containerViewId = R.id.abs__content;
            }
            if (fragment.mFragmentId != 0 && fragment.mFragmentId != containerViewId) {
                throw new IllegalStateException("Can't change container ID of fragment "
                        + fragment + ": was " + fragment.mFragmentId
                        + " now " + containerViewId);
            }
            fragment.mContainerId = fragment.mFragmentId = containerViewId;
        }

        Op op = new Op();
        op.cmd = opcmd;
        op.fragment = fragment;
        addOp(op);
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        if (containerViewId == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }

        doAddOp(containerViewId, fragment, tag, OP_REPLACE);
        return this;
    }

    public FragmentTransaction remove(Fragment fragment) {
        if (fragment.mImmediateActivity == null) {
            throw new IllegalStateException("Fragment not added: " + fragment);
        }
        fragment.mImmediateActivity = null;

        Op op = new Op();
        op.cmd = OP_REMOVE;
        op.fragment = fragment;
        addOp(op);

        return this;
    }

    public FragmentTransaction hide(Fragment fragment) {
        if (fragment.mImmediateActivity == null) {
            throw new IllegalStateException("Fragment not added: " + fragment);
        }

        Op op = new Op();
        op.cmd = OP_HIDE;
        op.fragment = fragment;
        addOp(op);

        return this;
    }

    public FragmentTransaction show(Fragment fragment) {
        if (fragment.mImmediateActivity == null) {
            throw new IllegalStateException("Fragment not added: " + fragment);
        }

        Op op = new Op();
        op.cmd = OP_SHOW;
        op.fragment = fragment;
        addOp(op);

        return this;
    }

    public FragmentTransaction detach(Fragment fragment) {
        //if (fragment.mImmediateActivity == null) {
        //    throw new IllegalStateException("Fragment not added: " + fragment);
        //}

        Op op = new Op();
        op.cmd = OP_DETACH;
        op.fragment = fragment;
        addOp(op);

        return this;
    }

    public FragmentTransaction attach(Fragment fragment) {
        //if (fragment.mImmediateActivity == null) {
        //    throw new IllegalStateException("Fragment not added: " + fragment);
        //}

        Op op = new Op();
        op.cmd = OP_ATTACH;
        op.fragment = fragment;
        addOp(op);

        return this;
    }

    public FragmentTransaction setCustomAnimations(int enter, int exit) {
        mEnterAnim = enter;
        mExitAnim = exit;
        return this;
    }

    public FragmentTransaction setTransition(int transition) {
        mTransition = transition;
        return this;
    }

    public FragmentTransaction setTransitionStyle(int styleRes) {
        mTransitionStyle = styleRes;
        return this;
    }

    public FragmentTransaction addToBackStack(String name) {
        if (!mAllowAddToBackStack) {
            throw new IllegalStateException(
                    "This FragmentTransaction is not allowed to be added to the back stack.");
        }
        mAddToBackStack = true;
        mName = name;
        return this;
    }

    public boolean isAddToBackStackAllowed() {
        return mAllowAddToBackStack;
    }

    public FragmentTransaction disallowAddToBackStack() {
        if (mAddToBackStack) {
            throw new IllegalStateException(
                    "This transaction is already being added to the back stack");
        }
        mAllowAddToBackStack = false;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(int res) {
        mBreadCrumbTitleRes = res;
        mBreadCrumbTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(CharSequence text) {
        mBreadCrumbTitleRes = 0;
        mBreadCrumbTitleText = text;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(int res) {
        mBreadCrumbShortTitleRes = res;
        mBreadCrumbShortTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(CharSequence text) {
        mBreadCrumbShortTitleRes = 0;
        mBreadCrumbShortTitleText = text;
        return this;
    }

    void bumpBackStackNesting(int amt) {
        if (!mAddToBackStack) {
            return;
        }
        if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Bump nesting in " + this
                + " by " + amt);
        Op op = mHead;
        while (op != null) {
            op.fragment.mBackStackNesting += amt;
            if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Bump nesting of "
                    + op.fragment + " to " + op.fragment.mBackStackNesting);
            if (op.removed != null) {
                for (int i=op.removed.size()-1; i>=0; i--) {
                    Fragment r = op.removed.get(i);
                    r.mBackStackNesting += amt;
                    if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Bump nesting of "
                            + r + " to " + r.mBackStackNesting);
                }
            }
            op = op.next;
        }
    }

    public int commit() {
        return commitInternal(false);
    }

    public int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    int commitInternal(boolean allowStateLoss) {
        if (mCommitted) throw new IllegalStateException("commit already called");
        if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Commit: " + this);
        mCommitted = true;
        if (mAddToBackStack) {
            mIndex = mManager.allocBackStackIndex(this);
        } else {
            mIndex = -1;
        }
        mManager.enqueueAction(this, allowStateLoss);
        return mIndex;
    }

    public void run() {
        if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Run: " + this);

        if (mAddToBackStack) {
            if (mIndex < 0) {
                throw new IllegalStateException("addToBackStack() called after commit()");
            }
        }

        bumpBackStackNesting(1);

        Op op = mHead;
        while (op != null) {
            switch (op.cmd) {
                case OP_ADD: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.enterAnim;
                    mManager.addFragment(f, false);
                } break;
                case OP_REPLACE: {
                    Fragment f = op.fragment;
                    if (mManager.mAdded != null) {
                        for (int i=0; i<mManager.mAdded.size(); i++) {
                            Fragment old = mManager.mAdded.get(i);
                            if (FragmentManagerImpl.DEBUG) Log.v(TAG,
                                    "OP_REPLACE: adding=" + f + " old=" + old);
                            if (old.mContainerId == f.mContainerId) {
                                if (op.removed == null) {
                                    op.removed = new ArrayList<Fragment>();
                                }
                                op.removed.add(old);
                                old.mNextAnim = op.exitAnim;
                                if (mAddToBackStack) {
                                    old.mBackStackNesting += 1;
                                    if (FragmentManagerImpl.DEBUG) Log.v(TAG, "Bump nesting of "
                                            + old + " to " + old.mBackStackNesting);
                                }
                                mManager.removeFragment(old, mTransition, mTransitionStyle);
                            }
                        }
                    }
                    f.mNextAnim = op.enterAnim;
                    mManager.addFragment(f, false);
                } break;
                case OP_REMOVE: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.exitAnim;
                    mManager.removeFragment(f, mTransition, mTransitionStyle);
                } break;
                case OP_HIDE: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.exitAnim;
                    mManager.hideFragment(f, mTransition, mTransitionStyle);
                } break;
                case OP_SHOW: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.enterAnim;
                    mManager.showFragment(f, mTransition, mTransitionStyle);
                } break;
                case OP_DETACH: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.exitAnim;
                    mManager.detachFragment(f, mTransition, mTransitionStyle);
                } break;
                case OP_ATTACH: {
                    Fragment f = op.fragment;
                    f.mNextAnim = op.enterAnim;
                    mManager.attachFragment(f, mTransition, mTransitionStyle);
                } break;
                default: {
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
                }
            }

            op = op.next;
        }

        mManager.moveToState(mManager.mCurState, mTransition,
                mTransitionStyle, true);

        if (mAddToBackStack) {
            mManager.addBackStackState(this);
        }
    }

    public void popFromBackStack(boolean doStateMove) {
        if (FragmentManagerImpl.DEBUG) Log.v(TAG, "popFromBackStack: " + this);

        bumpBackStackNesting(-1);

        Op op = mTail;
        while (op != null) {
            switch (op.cmd) {
                case OP_ADD: {
                    Fragment f = op.fragment;
                    f.mImmediateActivity = null;
                    mManager.removeFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition),
                            mTransitionStyle);
                } break;
                case OP_REPLACE: {
                    Fragment f = op.fragment;
                    f.mImmediateActivity = null;
                    mManager.removeFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition),
                            mTransitionStyle);
                    if (op.removed != null) {
                        for (int i=0; i<op.removed.size(); i++) {
                            Fragment old = op.removed.get(i);
                            old.mImmediateActivity = mManager.mActivity;
                            mManager.addFragment(old, false);
                        }
                    }
                } break;
                case OP_REMOVE: {
                    Fragment f = op.fragment;
                    f.mImmediateActivity = mManager.mActivity;
                    mManager.addFragment(f, false);
                } break;
                case OP_HIDE: {
                    Fragment f = op.fragment;
                    mManager.showFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition), mTransitionStyle);
                } break;
                case OP_SHOW: {
                    Fragment f = op.fragment;
                    mManager.hideFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition), mTransitionStyle);
                } break;
                case OP_DETACH: {
                    Fragment f = op.fragment;
                    mManager.attachFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition), mTransitionStyle);
                } break;
                case OP_ATTACH: {
                    Fragment f = op.fragment;
                    mManager.detachFragment(f,
                            FragmentManagerImpl.reverseTransit(mTransition), mTransitionStyle);
                } break;
                default: {
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
                }
            }

            op = op.prev;
        }

        if (doStateMove) {
            mManager.moveToState(mManager.mCurState,
                    FragmentManagerImpl.reverseTransit(mTransition), mTransitionStyle, true);
        }

        if (mIndex >= 0) {
            mManager.freeBackStackIndex(mIndex);
            mIndex = -1;
        }
    }

    public String getName() {
        return mName;
    }

    public int getTransition() {
        return mTransition;
    }

    public int getTransitionStyle() {
        return mTransitionStyle;
    }

    public boolean isEmpty() {
        return mNumOp == 0;
    }
}
