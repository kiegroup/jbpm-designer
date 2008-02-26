class Helper < ActiveRecord::Migration
  def self.up
    execute %q{
      create language plpgsql;
    }
    
    execute %q{
      create function identity(openid text) returns "identity" as $pgsql$
        declare
          result "identity";
        begin
          select * into result from "identity" where uri = openid;
          if not found then
            insert into "identity"(uri) values(openid) returning * into result;
          end if;
          
          return result;
        end;
      $pgsql$ language plpgsql;
    }
  end

end