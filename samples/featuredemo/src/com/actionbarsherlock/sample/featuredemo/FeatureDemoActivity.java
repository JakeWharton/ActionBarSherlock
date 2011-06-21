package com.actionbarsherlock.sample.featuredemo;

import java.util.Random;
import com.actionbarsherlock.sample.featuredemo.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class FeatureDemoActivity extends FragmentActivity {
	private static final Random RANDOM = new Random();
	
	private static boolean themeLight = false;
	private int items = 0;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		for (int i = 0; i < items; i++) {
			menu.add("Text")
				.setIcon(R.drawable.ic_title_share_default)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		
		menu.add("Model").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(FeatureDemoActivity.this, FeatureModel.class));
				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(themeLight ? R.style.Theme_Sherlock_Light : R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.feature_demo_activity);

        getSupportActionBar().setCustomView(R.layout.actionbar_custom_view);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        
        ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.simple_spinner_item);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(listAdapter, null);
        
        findViewById(R.id.display_theme_normal).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				themeLight = false;
				recreate();
			}
		});
        findViewById(R.id.display_theme_light).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				themeLight = true;
				recreate();
			}
		});
        
        findViewById(R.id.display_items_clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				items = 0;
				invalidateOptionsMenu();
			}
		});
        findViewById(R.id.display_items_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				items += 1;
				invalidateOptionsMenu();
			}
		});
        
        findViewById(R.id.display_subtitle_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setSubtitle("The quick brown fox jumps over the lazy dog.");
            }
        });
        findViewById(R.id.display_subtitle_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setSubtitle(null);
            }
        });
        
        findViewById(R.id.display_title_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
        });
        findViewById(R.id.display_title_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        });
        
        findViewById(R.id.display_custom_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowCustomEnabled(true);
            }
        });
        findViewById(R.id.display_custom_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowCustomEnabled(false);
            }
        });
        
        findViewById(R.id.navigation_standard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        });
        findViewById(R.id.navigation_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            }
        });
        findViewById(R.id.navigation_tabs).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
		});
        
        findViewById(R.id.display_home_as_up_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
        findViewById(R.id.display_home_as_up_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });
        
        findViewById(R.id.display_logo_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayUseLogoEnabled(true);
            }
        });
        findViewById(R.id.display_logo_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayUseLogoEnabled(false);
            }
        });
        
        findViewById(R.id.display_home_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        });
        findViewById(R.id.display_home_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        });
        
        findViewById(R.id.display_actionbar_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().show();
            }
        });
        findViewById(R.id.display_actionbar_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().hide();
            }
        });
        
        findViewById(R.id.display_tab_add).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.display_tab_select).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.display_tab_remove).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().removeTabAt(getSupportActionBar().getTabCount() - 1);
			}
		});
        findViewById(R.id.display_tab_remove_all).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getSupportActionBar().removeAllTabs();
			}
		});
    }
}