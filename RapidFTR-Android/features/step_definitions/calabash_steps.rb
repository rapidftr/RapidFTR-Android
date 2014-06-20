require 'calabash-android/calabash_steps'

Given(/^that I am logged in as "(.*?)" with password "(.*?)"$/) do |username, password|
  sleep(2)
  query('android.widget.EditText id:"username"', setText: '')
  query('android.widget.EditText id:"username"', setText: username)
  query('android.widget.EditText id:"password"', setText: '')
  query('android.widget.EditText id:"password"', setText: password)
  query('android.widget.EditText id:"url"', setText: '')
  query("android.widget.EditText id:'url'", setText: 'https://test.rapidftr.com')
  touch(query("android.widget.Button {text CONTAINS[c] 'Log In'}"))
  macro 'I wait up to 20 seconds for "Basic Identity" to appear'
end

When(/^I enter "(.*?)" into the "(.*?)" field of the Child Registration Form$/) do |text, field_name|
  sleep(2)
  query('EditText', :setText => text)
end