require 'net/http'
require 'uri'


url = URI.parse('http://localhost:8080/poem-backend-1.0/poem/data/model/10')
res = Net::HTTP.start(url.host, url.port) do |http|
  body = "title=The Spec edits the Title&type=BPEL4CHOR&summary=test successfull"
  http.send_request('POST', 'http://localhost:8080/poem-backend-1.0/poem/data/model/10', body)
end
puts res.code
