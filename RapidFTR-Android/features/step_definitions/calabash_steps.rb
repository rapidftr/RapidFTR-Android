require 'calabash-android/calabash_steps'
require 'mimic'

Given(/^that I am logged in as "(.*?)" with password "(.*?)"$/) do |username, password|
  mimic.post("/api/login") do
    [201, {}, '{"db_key":"9d5994dd5da322d0","organisation":"N/A","language":"en","verified":true}']
  end
  performAction('clear_id_field','username')
  performAction('enter_text_into_id_field', username, 'username')
  performAction('clear_id_field','password')
  performAction('enter_text_into_id_field', password, 'password')
  performAction('clear_id_field','url')
  performAction('enter_text_into_id_field', $WEB_URL, 'url')
  performAction('press',"Log In")
  performAction('wait_for_text', 'Basic Identity', 20)
end


When(/^I enter "(.*?)" into the "(.*?)" field of the Child Registration Form$/) do |text, field_name|
  sleep(2)
  query('EditText', :setText => text)
end


Then(/^I should see the url used for the last successful login$/) do
  performAction('assert_text', $WEB_URL, true)
end


When(/^I press Change Password$/) do
  mimic.post "/users/update_password" do
    [200, {}, 'OK']
  end
  performAction('press',"Change Password")
end


When(/^the credentials are "(.*?)"$/) do |state|
  mimic.clear
  if state=='valid' then
    mimic.post "/users/update_password" do
      [200, {}, 'OK']
    end
  end
end