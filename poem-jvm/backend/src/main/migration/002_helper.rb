##############################
 # Copyright (c) 2008
 # Ole Eckermann, Hagen Overdick
 #
 # Permission is hereby granted, free of charge, to any person obtaining a
 # copy of this software and associated documentation files (the "Software"),
 # to deal in the Software without restriction, including without limitation
 # the rights to use, copy, modify, merge, publish, distribute, sublicense,
 # and/or sell copies of the Software, and to permit persons to whom the
 # Software is furnished to do so, subject to the following conditions:
 #
 # The above copyright notice and this permission notice shall be included in
 # all copies or substantial portions of the Software.
 #
 # THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 # IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 # FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 # AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 # LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 # FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 # DEALINGS IN THE SOFTWARE.
 ##############################

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
          select * into result 
          from "identity" 
          where uri = openid;
          
          if not found then
            insert into "identity"(uri) values(openid) returning * into result;
          end if;
          
          return result;
        end;
      $pgsql$ language plpgsql;
    }
    
    execute %q{
      create function ensure_descendant(root_hierarchy text, target integer) returns "structure" as $pgsql$
        declare
          result "structure";
        begin
          lock structure;
          
          select * into result
          from "structure"
          where hierarchy like root_hierarchy || '%'
                          and ident_id = target;
          
          if not found then
            insert into "structure"(hierarchy, ident_id) values(next_child_position(root_hierarchy), target) returning * into result;
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
      
      create function next_child_position(hierarchy text) returns text as $sql$
        select $1 || encode_position(coalesce(max(child_position(hierarchy))+1,0)) from "structure" where parent(hierarchy) = $1;
      $sql$ language sql;
      
      create index poem_position on structure(parent(hierarchy), child_position(hierarchy));
    }
    

    
    execute %q{
      create or replace view access as
        select  context_name.id     as context_id,
                context_name.uri    as context_name,
                subject_name.id     as subject_id,
                subject_name.uri    as subject_name,
                object_name.id      as object_id,
                object_name.uri     as object_name,
                access.id           as access_id,
                access.scheme		    as access_scheme,	
                access.term			    as access_term,
                plugin.rel			    as plugin_relation,
                plugin.scheme		    as scheme,
                plugin.term			    as term
        from    "interaction" as access,
                "structure"   as context,
                "identity"    as context_name,
                "structure"   as subject_axis,
                "identity"    as subject_name,
                "structure"   as object_axis,
                "identity"    as object_name,
                "plugin"	    as plugin
        where   access.subject = context.hierarchy
            and context.ident_id = context_name.id
            and (     access.subject = subject_axis.hierarchy
                  or  (access.subject_descend and subject_axis.hierarchy like access.subject || '_%')
                )
            and (
              ((not access.object_restrict_to_parent) and access.object_self and access.object = object_axis.hierarchy)
              or  ((not access.object_restrict_to_parent) and access.object_descend and object_axis.hierarchy like access.object || '_%')
              or ((access.object_restrict_to_parent and access.object_self) and object_axis.hierarchy = subject_axis.hierarchy)
              or ((access.object_restrict_to_parent and access.object_descend) and parent(object_axis.hierarchy) = subject_axis.hierarchy)
            )
            and subject_axis.ident_id = subject_name.id
            and object_axis.ident_id = object_name.id;
    }
  end

end