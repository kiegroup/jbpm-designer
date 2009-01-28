require 'net/http'
require 'uri'

url = URI.parse('http://localhost:3000/data/model')
res = Net::HTTP.start(url.host, url.port) do |http|
  http.send_request('GET', url.to_s, nil,{'QUERY_STRING'=>'date=this_week'})
end

puts res.message