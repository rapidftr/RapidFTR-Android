require 'webmock'
require 'webmock/server'

Thread.new do
  WebMock::Server.start Port: 2000
end

WebMock.allow_net_connect!
World(WebMock::Server::API)

Before do
  WebMock.reset!
end
