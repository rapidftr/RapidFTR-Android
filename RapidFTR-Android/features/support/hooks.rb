require 'net/https'
require 'uri'
require 'httparty'

$WEB_URL = ENV['WEB_URL']
WEB_USER = 'rapidftr'
WEB_PASSWORD = 'rapidftr'

if $WEB_URL == 'https://test.rapidftr.com' then
  Before('@reinstall') do
  	reset_database
  end
  
  at_exit do
    reset_database
  end
end

def reset_database
  puts "Reseting Web App State..."
  response = HTTParty.post "#{$WEB_URL}/api/login?user_name=#{WEB_USER}&password=#{WEB_PASSWORD}&imei=0000000000&mobile_number=0000000000", verify: false
  puts response.headers['status']
  response = HTTParty.delete "#{$WEB_URL}/database/delete_data/child", headers: { 'Cookie' => response.headers['Set-Cookie'] }, verify: false
  puts response.headers['status']
  response = HTTParty.delete "#{$WEB_URL}/database/delete_data/enquiry", headers: { 'Cookie' => response.headers['Set-Cookie'] }, verify: false
  puts response.headers['status']
  response = HTTParty.delete "#{$WEB_URL}/database/reset_fieldworker", headers: { 'Cookie' => response.headers['Set-Cookie'] }, verify: false
  puts response.headers['status']
  response = HTTParty.post "#{$WEB_URL}/api/logout", headers: { 'Cookie' => response.headers['Set-Cookie'] }, verify: false
  puts response.headers['status']
end