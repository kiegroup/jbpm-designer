##############################
 # Copyright (c) 2008
 # Ole Eckermann, Bjoern Wagner, Hagen Overdick
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
       create language plpythonu;
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

     execute %q{

 create function work_around_path(hierarchy text) returns setof text as $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all

   return poem_path(hierarchy)[2:-1]
 $$ language plpythonu immutable;

 create or replace function poem_path(hierarchy text) returns setof text as $$
 # Returns an ordered collection of strings. The first item of the collection is
 # the path to the first item in the hierarchy and so on.
 all = ["",""]
 if (hierarchy == ""):
 	return all
 else:
 	position = 0
 	# for each character in the input string
 	while position < len(hierarchy):
 		# count tildes
 		tilde_count = 0
 		while hierarchy[position+tilde_count] == "~":
 			tilde_count += 1
 		if tilde_count == 0:
 			all.append(hierarchy[:position+1])
 			position += 1
 		else:
 			all.append(hierarchy[:position+2*tilde_count])
 		position += 2*tilde_count;
 	return all
 $$ language plpythonu immutable;

 create function parent(hierarchy text) returns text as $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all

 # Returns path to parent item
 return poem_path(hierarchy)[-2]
 $$ language plpythonu immutable;

 create function encode_position(positio integer) returns text as $$
 # Encodes number to string representation
 # 0: "0" = 0, "z" = 61
 # 1: "~0" = 62, "~z" = 123
 # 2: "~~00" = 124, "~~zz" = 3967
 # 3: "~~~000" = 3968, "~~~zzz" = 242295
 position = positio
 if position in range(0,10):
 	return str(position)
 if position in range(10,36):
  	return chr(position+55)
 if position in range(16,62):
     return chr(position+61)
 if position in range(62,124):
     return "~" + encode_position(position-62)
 if position in range(124, 3968):
     return "~~" + encode_position((position-124)/62) + encode_position((position-124)%62) 
 if position in range(3968, 242296):
 	position -= 3968
 	digit1 = position / (62 * 62)
 	digit2 = (position % (62 * 62)) / 62
 	digit3 = (position % (62 * 62)) % 62
 	return "~~~" + encode_position(digit1) + encode_position(digit2) + encode_position(digit3)
 raise "Stored Procedure: Encode Postition: Position out of range." 
 $$ language plpythonu immutable;

 create function decode_position(code text) returns integer as $$
 # Decodes the input code string to the original number
 offset = 0
 if (code == ""):
 	return None
 while (code[offset] == "~"):
     offset+=1
 digits = []    
 for digit in code[offset:]:
 	digit_no = ord(digit)
 	if digit_no in range(48,58):
 		digits.append(digit_no-48)
 	if digit_no in range(65, 91):
 		digits.append(digit_no-55)
 	if digit_no in range(97,123):
 		digits.append(digit_no-61)

 if offset ==  0:
     return digits[0]
 if offset ==  1:
     return digits[0] + 62
 if offset ==  2:
     return 124 + digits[0] * 62 + digits[1]
 if offset == 3:
 	return 3968 + digits[0] * 62 * 62 + digits[1] * 62 + digits[2]
 $$ language plpythonu immutable;

 create function child_position(hierarchy text) returns integer as $$
 def poem_path(hierarchy):
 	# Returns an ordered collection of strings. The first item of the collection is
 	# the path to the first item in the hierarchy and so on.
 	all = ["",""]
 	if (hierarchy == ""):
 		return all
 	else:
 		position = 0
 		# for each character in the input string
 		while position < len(hierarchy):
 			# count tildes
 			tilde_count = 0
 			while hierarchy[position+tilde_count] == "~":
 				tilde_count += 1
 			if tilde_count == 0:
 				all.append(hierarchy[:position+1])
 				position += 1
 			else:
 				all.append(hierarchy[:position+2*tilde_count])
 			position += 2*tilde_count;
 		return all

 def decode_position(code):
 	# Decodes the input code string to the original number
 	offset = 0
 	if (code == ""):
 		return None
 	while (code[offset] == "~"):
 	    offset+=1
 	digits = []    
 	for digit in code[offset:]:
 		digit_no = ord(digit)
 		if digit_no in range(48,58):
 			digits.append(digit_no-48)
 		if digit_no in range(65, 91):
 			digits.append(digit_no-55)
 		if digit_no in range(97,123):
 			digits.append(digit_no-61)

 	if offset ==  0:
 	    return digits[0]
 	if offset ==  1:
 	    return digits[0] + 62
 	if offset ==  2:
 	    return 124 + digits[0] * 62 + digits[1]
 	if offset == 3:
 		return 3968 + digits[0] * 62 * 62 + digits[1] * 62 + digits[2]

 path = poem_path(hierarchy)
 parent = -2
 current = -1
 local = path[current][len(path[parent]):] # == current-parent
 return decode_position(local)
 $$ language plpythonu immutable;

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
