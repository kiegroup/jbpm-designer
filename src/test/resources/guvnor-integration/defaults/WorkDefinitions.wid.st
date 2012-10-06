import org.drools.process.core.datatype.impl.type.StringDataType;
[
  [
    "name" : "Email",
    "extra" : "This String Verifies That The Correct wid was Loaded",
    "parameters" : [
      "From" : new StringDataType(),
      "To" : new StringDataType(),
      "Subject" : new StringDataType(),
      "Body" : new StringDataType()
    ],
    "displayName" : "Email",
    "icon" : "$protocol$://$host$/$subdomain$/rest/packages/$pkgName$/assets/defaultemailicon/binary"
  ],
  
  [
    "name" : "Log",
    "parameters" : [
      "Message" : new StringDataType()
      "Message" : new StringDataType()
    ],
    "displayName" : "Log",
    "icon" : "$protocol$://$host$/$subdomain$/rest/packages/$pkgName$/assets/defaultlogicon/binary"
  ]
]
