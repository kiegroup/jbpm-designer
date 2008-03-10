require 'handler'
  module Handler
    class InfoHandler < DefaultHandler
      @@map_date = 
      { 'today'           => %Q{and r.updated >= current_date},
      'last_seven_days'   => %Q{and r.updated >= current_date-integer '7'},
      'last_thirty_days'  => %Q{and r.updated >= current_date-integer '30' and r.updated < current_date-integer '7'},
      'this_year'         => %Q{and extract(year from r.updated) = extract(year from current_date) and r.updated < current_date-integer '30'}}
    def doGet(interaction)
    end
  end
end