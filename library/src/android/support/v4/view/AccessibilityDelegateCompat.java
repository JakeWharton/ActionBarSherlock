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

package android.support.v4.view;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

/**
 * Helper for accessing AccessibilityDelegate from newer platform versions.
 */
public class AccessibilityDelegateCompat {

    static interface AccessibilityDelegateImpl {
        public Object newAccessiblityDelegateDefaultImpl();
        public Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat listener);
        public boolean dispatchPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event);
        public void onInitializeAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event);
        public void onInitializeAccessibilityNodeInfo(Object delegate, View host, Object info);
        public void onPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event);
        public boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child,
                AccessibilityEvent event);
        public void sendAccessibilityEvent(Object delegate, View host, int eventType);
        public void sendAccessibilityEventUnchecked(Object delegate, View host,
                AccessibilityEvent event);
    }

    static class AccessibilityDelegateStubImpl implements AccessibilityDelegateImpl {
        public Object newAccessiblityDelegateDefaultImpl() {
            return null;
        }

        public Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat listener) {
            return null;
        }

        public boolean dispatchPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {
            return false;
        }

        public void onInitializeAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {

        }

        public void onInitializeAccessibilityNodeInfo(Object delegate, View host, Object info) {

        }

        public void onPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {

        }

        public boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child,
                AccessibilityEvent event) {
            return true;
        }

        public void sendAccessibilityEvent(Object delegate, View host, int eventType) {

        }

        public void sendAccessibilityEventUnchecked(Object delegate, View host,
                AccessibilityEvent event) {

        }
    }
/*
    static class AccessibilityDelegateIcsImpl extends AccessibilityDelegateStubImpl {
        @Override
        public Object newAccessiblityDelegateDefaultImpl() {
            return AccessibilityDelegateCompatIcs.newAccessibilityDelegateDefaultImpl();
        }

        @Override
        public Object newAccessiblityDelegateBridge(final AccessibilityDelegateCompat compat) {
            return AccessibilityDelegateCompatIcs.newAccessibilityDelegateBridge(
                    new AccessibilityDelegateCompatIcs.AccessibilityDelegateBridge() {
                @Override
                public boolean dispatchPopulateAccessibilityEvent(View host,
                        AccessibilityEvent event) {
                    return compat.dispatchPopulateAccessibilityEvent(host, event);
                }

                @Override
                public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onInitializeAccessibilityEvent(host, event);
                }

                @Override
                public void onInitializeAccessibilityNodeInfo(View host, Object info) {
                    compat.onInitializeAccessibilityNodeInfo(host,
                            new AccessibilityNodeInfoCompat(info));
                }

                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    compat.onPopulateAccessibilityEvent(host, event);
                }

                @Override
                public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
                        AccessibilityEvent event) {
                    return compat.onRequestSendAccessibilityEvent(host, child, event);
                }

                @Override
                public void sendAccessibilityEvent(View host, int eventType) {
                    compat.sendAccessibilityEvent(host, eventType);
                }

                @Override
                public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                    compat.sendAccessibilityEventUnchecked(host, event);
                }
            });
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {
            return AccessibilityDelegateCompatIcs.dispatchPopulateAccessibilityEvent(delegate,
                    host, event);
        }

        @Override
        public void onInitializeAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {
            AccessibilityDelegateCompatIcs.onInitializeAccessibilityEvent(delegate, host, event);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(Object delegate, View host, Object info) {
            AccessibilityDelegateCompatIcs.onInitializeAccessibilityNodeInfo(delegate, host, info);
        }

        @Override
        public void onPopulateAccessibilityEvent(Object delegate, View host,
                AccessibilityEvent event) {
            AccessibilityDelegateCompatIcs.onPopulateAccessibilityEvent(delegate, host, event);
        }

        @Override
        public boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child,
                AccessibilityEvent event) {
            return AccessibilityDelegateCompatIcs.onRequestSendAccessibilityEvent(delegate, host,
                    child, event);
        }

        @Override
        public void sendAccessibilityEvent(Object delegate, View host, int eventType) {
            AccessibilityDelegateCompatIcs.sendAccessibilityEvent(delegate, host, eventType);
        }

        @Override
        public void sendAccessibilityEventUnchecked(Object delegate, View host,
                AccessibilityEvent event) {
            AccessibilityDelegateCompatIcs.sendAccessibilityEventUnchecked(delegate, host, event);
        }
    }
*/
    private static final AccessibilityDelegateImpl IMPL;
    private static final Object DEFAULT_DELEGATE;

    static {
        //if (Build.VERSION.SDK_INT >= 14) { // ICS
        //    IMPL = new AccessibilityDelegateIcsImpl();
        //} else {
            IMPL = new AccessibilityDelegateStubImpl();
        //}
        DEFAULT_DELEGATE = IMPL.newAccessiblityDelegateDefaultImpl();
    }

    final Object mBridge;

    /**
     * Creates a new instance.
     */
    public AccessibilityDelegateCompat() {
        mBridge = IMPL.newAccessiblityDelegateBridge(this);
    }

    /**
     * @return The wrapped bridge implementation.
     */
    Object getBridge() {
        return mBridge;
    }

    /**
     * Sends an accessibility event of the given type. If accessibility is not
     * enabled this method has no effect.
     * <p>
     * The default implementation behaves as {@link View#sendAccessibilityEvent(int)
     * View#sendAccessibilityEvent(int)} for the case of no accessibility delegate
     * been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param eventType The type of the event to send.
     *
     * @see View#sendAccessibilityEvent(int) View#sendAccessibilityEvent(int)
     */
    public void sendAccessibilityEvent(View host, int eventType) {
        IMPL.sendAccessibilityEvent(DEFAULT_DELEGATE, host, eventType);
    }

    /**
     * Sends an accessibility event. This method behaves exactly as
     * {@link #sendAccessibilityEvent(View, int)} but takes as an argument an
     * empty {@link AccessibilityEvent} and does not perform a check whether
     * accessibility is enabled.
     * <p>
     * The default implementation behaves as
     * {@link View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     * View#sendAccessibilityEventUnchecked(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event to send.
     *
     * @see View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     *      View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     */
    public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
        IMPL.sendAccessibilityEventUnchecked(DEFAULT_DELEGATE, host, event);
    }

    /**
     * Dispatches an {@link AccessibilityEvent} to the host {@link View} first and then
     * to its children for adding their text content to the event.
     * <p>
     * The default implementation behaves as
     * {@link View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     * View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event.
     * @return True if the event population was completed.
     *
     * @see View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     *      View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     */
    public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        return IMPL.dispatchPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    /**
     * Gives a chance to the host View to populate the accessibility event with its
     * text content.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onPopulateAccessibilityEvent(View, AccessibilityEvent)
     * ViewCompat#onPopulateAccessibilityEvent(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The accessibility event which to populate.
     *
     * @see ViewCompat#onPopulateAccessibilityEvent(View ,AccessibilityEvent)
     *      ViewCompat#onPopulateAccessibilityEvent(View, AccessibilityEvent)
     */
    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    /**
     * Initializes an {@link AccessibilityEvent} with information about the
     * the host View which is the event source.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onInitializeAccessibilityEvent(View v, AccessibilityEvent event)
     * ViewCompat#onInitalizeAccessibilityEvent(View v, AccessibilityEvent event)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event to initialize.
     *
     * @see ViewCompat#onInitializeAccessibilityEvent(View, AccessibilityEvent)
     *      ViewCompat#onInitializeAccessibilityEvent(View, AccessibilityEvent)
     */
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onInitializeAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    /**
     * Initializes an {@link AccessibilityNodeInfoCompat} with information about the host view.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     * ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param info The instance to initialize.
     *
     * @see ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     *      ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     */
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        IMPL.onInitializeAccessibilityNodeInfo(DEFAULT_DELEGATE, host, info);
    }

    /**
     * Called when a child of the host View has requested sending an
     * {@link AccessibilityEvent} and gives an opportunity to the parent (the host)
     * to augment the event.
     * <p>
     * The default implementation behaves as
     * {@link ViewGroupCompat#onRequestSendAccessibilityEvent(ViewGroup, View, AccessibilityEvent)
     * ViewGroupCompat#onRequestSendAccessibilityEvent(ViewGroup, View, AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param child The child which requests sending the event.
     * @param event The event to be sent.
     * @return True if the event should be sent
     *
     * @see ViewGroupCompat#onRequestSendAccessibilityEvent(ViewGroup, View, AccessibilityEvent)
     *      ViewGroupCompat#onRequestSendAccessibilityEvent(ViewGroup, View, AccessibilityEvent)
     */
    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
            AccessibilityEvent event) {
        return IMPL.onRequestSendAccessibilityEvent(DEFAULT_DELEGATE, host, child, event);
    }
}
