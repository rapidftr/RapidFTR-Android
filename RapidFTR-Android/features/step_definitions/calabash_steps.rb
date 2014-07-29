require 'calabash-android/calabash_steps'
require 'mimic'

When(/^I enter the username "(.*?)" and password "(.*?)"$/) do |username, password|
  performAction('clear_id_field','username')
  performAction('enter_text_into_id_field', username, 'username')
  performAction('clear_id_field','password')
  performAction('enter_text_into_id_field', password, 'password')
end


Given(/^that I am logged in as "(.*?)" with password "(.*?)"$/) do |username, password|
  mimic.clear
  mimic.post("/api/login") do
    [201, {}, '{"db_key":"9d5994dd5da322d0","organisation":"N/A","language":"en","verified":true}']
  end
  macro "I enter the username \"#{username}\" and password \"#{password}\""
  performAction('clear_id_field','url')
  performAction('enter_text_into_id_field', $WEB_URL, 'url')
  performAction('press',"Log In")
  performAction('wait_for_text', 'Basic Identity', 20)
end


When(/^I enter the Web Server URL$/) do
  performAction('enter_text_into_id_field', $WEB_URL, 'url')
end


When(/^I fill the Change Password Form with "(.*?)", "(.*?)", "(.*?)"$/) do |currentpass, newpass, newpassconfirmation|
  performAction('select_from_menu', 'Change Password')
  performAction('enter_text_into_id_field', currentpass, 'current_password')
  performAction('enter_text_into_id_field', newpass, 'new_password')
  performAction('enter_text_into_id_field', newpassconfirmation, 'new_password_confirm')
  mimic.post "/users/update_password" do
    [200, {}, 'OK']
  end
  performAction('press',"Change Password")
end


When(/^I enter "(.*?)" into the "(.*?)" field of the Child Registration Form$/) do |text, field_name|
  sleep(2)
  query('EditText', :setText => text)
end


Then(/^I should see the url used for the last successful login$/) do
  performAction('assert_text', $WEB_URL, true)
end


When(/^I login with the "(.*?)" credentials "(.*?)" and password "(.*?)"$/) do |state, username, password|
  mimic.clear
  mimic.post("/api/login") do
      [401, {}, 'Invalid credentials. Please try again!']
  end

  if state=='valid' then
    mimic.post("/api/login") do
      [201, {}, '{"db_key":"9d5994dd5da322d0","organisation":"N/A","language":"en","verified":true}']
    end
  end
  macro "I enter the username \"#{username}\" and password \"#{password}\""
  performAction('press',"Log In")
end


Then(/^I should see all the default form sections$/) do
  default_form_sections = [
    "Family details", 
    "Care Arrangements", 
    "Separation History", 
    "Protection Concerns", 
    "Other Interviews", 
    "Other Tracing Info",
    "Interview Details",
    "Photos and Audio"
  ]
  
  default_form_sections.each do |section|
    performAction('assert_text', section, true)
  end
end

Then(/^I should see all the Child Registration Tabs$/) do
  child_tabs = [
    "Child",
    "Enquiry",
    "Register",
    "View All",
    "Search"
  ]

  child_tabs.each do |tab|
    performAction('assert_text', tab, true)
  end
end

Then(/^I should see the following fields$/) do |fields_table|
  fields_table.hashes.each do |field|
    performAction('assert_text', field['fieldname'], true)
  end
end

When(/^I press enter$/) do
  system("adb shell input keyevent KEYCODE_ENTER")
end

And(/^I enter the following form details$/) do |form_details|
  form_details.hashes.each do |field|
    performAction('clear_id_field', field['fieldname'])
    performAction('enter_text_into_id_field', field['details'], field['fieldname'])
  end
end

When(/^I press the menu button$/) do
  system("adb shell input keyevent KEYCODE_MENU")
end

Then(/^I should see "(.*?)" within "(.*?)" seconds$/) do |text, timeout|
  performAction('wait_for_text', text, timeout)
end