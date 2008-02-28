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
    
    # PL/Ruby
    # Assumes the script to run local on your db, change if necessary (full path to plruby.{so,bundle,dll,sl})
    plruby_lib = Dir.glob("{#{($LOAD_PATH).join(',')}}/plruby.{so,bundle,dll,sl}")[0]
    begin
      execute <<-SQL
        create function plruby_call_handler() returns language_handler
        as '#{plruby_lib}'
        language 'C';
        
        create trusted language 'plruby' handler plruby_call_handler lancompiler 'PL/Ruby';
      SQL
    rescue => ex
      puts "ERROR:"
      puts ex
      puts "Hint: Either you don't have pl/ruby or your are installing remotely, you will need to edit db/migration/001_bootstrap.rb"
    end
    
    create_table 'public.plruby_singleton_methods' do |t|
      t.column :name, :text, :null => false
      t.column :args, :text, :null => false
      t.column :body, :text, :null => false
    end
    
    execute %q{
      insert into plruby_singleton_methods(name, args, body) values('poem_path', 'hierarchy', $ruby$
        head = ''
        all = ['', '']
        chars = hierarchy.split('')
        return all unless chars
        while chars.size > 0
          tilde_count = 0
          tilde_count += 1 while chars[tilde_count] == '~'
          if tilde_count > 0
            offset = 2 * tilde_count
            head += chars[0...offset].to_s
            chars = chars[offset..-1]
          else
            head += chars.delete_at(0)
          end
          all << head
        end
        return all
      $ruby$);
      
      insert into plruby_singleton_methods(name, args, body) values('parent', 'hierarchy', $ruby$
        poem_path(hierarchy)[-2]
      $ruby$);
      
      insert into plruby_singleton_methods(name, args, body) values('encode_position', 'position', $ruby$
        #0: "0" = 0, "z" = 61
        #1: "~0" = 62, "~z" = 123
        #2: "~~00" = 124, "~~zz" = 3967
        #3: "~~~000" = 3968, "~~~zzz" = 242295
        case position
        when 0..9:
          position.to_s
        when 10..35:
          (position+55).chr
        when 36..61:
          (position+61).chr
        when 62..123:
          "~#{encode_position(position-62)}"
        when 124..3967:
          raw = position-124
          "~~#{encode_position(raw / 62)}#{encode_position(raw % 62)}"
        else
          raise :todo
        end
      $ruby$);
      
      insert into plruby_singleton_methods(name, args, body) values('decode_position', 'code', $ruby$
        offset = code.scan(/~*/)[0].size        
      
        digits = code[offset..-1].split('').collect do |digit|
          case digit # check ascii, if this doesn't make sense
          when '0'..'9':
            digit.to_i
          when 'A'..'Z':
            digit[0]-55
          when 'a'..'z':
            digit[0]-61
          else
            raise 'Unknown encoding'
          end
        end
        
        case offset
        when 0:
          digits[0]
        when 1:
          digits[0] + 62
        when 2:
          124 + digits[0] * 62 + digits[1]
        else
          raise :todo
        end
      $ruby$);
    }
    
    execute %q{
      insert into plruby_singleton_methods(name, args, body) values('child_position', 'hierarchy', $ruby$
        path = poem_path(hierarchy)
        parent = -2
        current = -1

        local = path[current][path[parent].size..-1] # == current-parent

        decode_position(local)
      $ruby$);
    }
      
    execute %q{
      
      create function work_around_path(hierarchy text) returns setof text as $ruby$
        poem_path(hierarchy).each{|val| yield val unless val == ''}
      $ruby$ language plruby immutable;
      
      create or replace function poem_path(hierarchy text) returns setof text as $sql$
        select '' union all select '' union all select * from work_around_path($1);
      $sql$ language sql immutable;
      
      create function parent(hierarchy text) returns text as $ruby$
        parent(hierarchy)
      $ruby$ language plruby immutable;
      
      create function encode_position(pos integer) returns text as $ruby$
        encode_position(pos)
      $ruby$ language plruby immutable;
      
      create function decode_position(code text) returns integer as $ruby$
        decode_position(code)
      $ruby$ language plruby immutable;
      
      create function child_position(hierarchy text) returns integer as $ruby$
        child_position(hierarchy)
      $ruby$ language plruby immutable;
      
      create function next_child_position(parent_hier text) returns text as $sql$
        select $1 || encode_position(coalesce(max(child_position(hierarchy))+1,0)) from structure where parent(hierarchy) = $1;
      $sql$ language sql;
      
      create index poem_parent on structure(parent(hierarchy));
      create index poem_childpos on structure(child_position(hierarchy));
    }
    
    execute %q{
      create function ensure_descendant(root_hierarchy text, target identity) returns structure as $pgsql$
        declare
          result structure;
        begin
          select  * 
          into    result
          from    structure
          where   hierarchy like root_hierarchy || '%'
              and identity = target.id;
          
          if not found then
            insert into structure(hierarchy, identity)
              select next_child_position(root_hierarchy), target.id
              returning * into result;
          end if;
          
          return result;
        end;
      $pgsql$ language plpgsql;
    }
  end

end