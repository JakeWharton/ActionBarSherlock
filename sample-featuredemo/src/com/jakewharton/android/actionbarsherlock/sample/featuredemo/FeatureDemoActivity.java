package com.jakewharton.android.actionbarsherlock.sample.featuredemo;

import java.util.Random;
import com.jakewharton.android.actionbarsherlock.sample.featuredemo.R;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class FeatureDemoActivity extends Activity {
	private static final Random RANDOM = new Random();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.feature_demo_activity);

        this.getSupportActionBar().setCustomView(R.layout.actionbar_custom_view);
        this.getSupportActionBar().setDisplayShowCustomEnabled(false);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        
        SpinnerAdapter listAdapter = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.actionbar_title);
        this.getSupportActionBar().setListNavigationCallbacks(listAdapter, null);
        
        this.findViewById(R.id.display_subtitle_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setSubtitle("The quick brown fox jumps over the lazy dog.");
            }
        });
        this.findViewById(R.id.display_subtitle_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setSubtitle(null);
            }
        });
        
        this.findViewById(R.id.display_title_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
        });
        this.findViewById(R.id.display_title_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        });
        
        this.findViewById(R.id.display_custom_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowCustomEnabled(true);
            }
        });
        this.findViewById(R.id.display_custom_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowCustomEnabled(false);
            }
        });
        
        this.findViewById(R.id.navigation_standard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        });
        this.findViewById(R.id.navigation_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            }
        });
        this.findViewById(R.id.navigation_tabs).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
		});
        
        this.findViewById(R.id.display_home_as_up_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
        this.findViewById(R.id.display_home_as_up_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });
        
        this.findViewById(R.id.display_logo_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
        });
        this.findViewById(R.id.display_logo_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayUseLogoEnabled(false);
            }
        });
        
        this.findViewById(R.id.display_home_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        });
        this.findViewById(R.id.display_home_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        });
        
        this.findViewById(R.id.display_actionbar_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().show();
            }
        });
        this.findViewById(R.id.display_actionbar_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().hide();
            }
        });
        
        this.findViewById(R.id.display_tab_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ActionBar.Tab newTab = getSupportActionBar().newTab();
				
				if (RANDOM.nextBoolean()) {
					newTab.setCustomView(R.layout.actionbar_tab_custom_view);
				} else {
					boolean icon = RANDOM.nextBoolean();
					if (icon) {
						newTab.setIcon(R.drawable.ic_title_share_default);
					}
					if (!icon || RANDOM.nextBoolean()) {
						newTab.setText("Text!");
					}
				}
				
				getSupportActionBar().addTab(newTab);
			}
		});
        this.findViewById(R.id.display_tab_select).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getSupportActionBar().getTabCount() > 0) {
					getSupportActionBar().selectTab(
							getSupportActionBar().getTabAt(
									RANDOM.nextInt(getSupportActionBar().getTabCount())
							)
					);
				}
			}
		});
        this.findViewById(R.id.display_tab_remove).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().removeTabAt(getSupportActionBar().getTabCount() - 1);
			}
		});
        this.findViewById(R.id.display_tab_remove_all).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().removeAllTabs();
			}
		});
    }
}