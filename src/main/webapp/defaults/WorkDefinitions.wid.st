import org.drools.process.core.datatype.impl.type.StringDataType;
[
  [
    "name" : "Email",
    "parameters" : [
      "From" : new StringDataType(),
      "To" : new StringDataType(),
      "Subject" : new StringDataType(),
      "Body" : new StringDataType()
    ],
    "displayName" : "Email",
    "icon" : "/global/defaultemailicon.gif"
  ],
  
  [
    "name" : "Log",
    "parameters" : [
      "Message" : new StringDataType()
    ],
    "displayName" : "Log",
    "icon" : "/global/defaultlogicon.gif"
  ]
]
