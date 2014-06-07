require 'calabash-android/management/adb'
require 'calabash-android/operations'

Before do |scenario|
	scenario_tags = scenario.source_tag_names
	start_test_server_in_background
end

After do |scenario|
  if scenario.failed?
    screenshot_embed
  end
  shutdown_test_server
end