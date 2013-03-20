<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <link rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/visualassignments.css" type="text/css" />
    <link rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/jquery-ui.css" type="text/css" />

    <script type='text/javascript' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-1.8.1.min.js'></script>
    <script type='text/javascript' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-ui-1.8.23.min.js'></script>
    <script type='text/javascript' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jsPlumb-1.3.16-all-min.js'></script>
</head>
<body bgcolor="#FFFFFF">
    <script>
        alert('vars: <%=request.getParameter("vars")%>');
        alert('globals: <%=request.getParameter("globals")%>');
        alert('data objects: <%=request.getParameter("dobj")%>');
    </script>

    <div style="position:relative;margin-top:100px;">
        <div id="demo">
            <div class="window" id="window1"><strong>My User Task</strong><br/><br/></div>
            <div class="window" id="window2"><strong>PVar 2</strong><br/><br/></div>
            <div class="window" id="window3"><strong>PVar 3</strong><br/><br/></div>
            <div class="window" id="window4"><strong>PVar 4</strong><br/><br/></div>
            <div class="window" id="window5"><strong>PVar 5</strong><br/><br/></div>
        </div>
    </div>

    <script>
        ;(function() {

            window.jsPlumbDemo = {
                init : function() {

                    jsPlumb.importDefaults({
                        // default drag options
                        DragOptions : { cursor: 'pointer', zIndex:2000 },
                        // default to blue at one end and green at the other
                        EndpointStyles : [{ fillStyle:'#225588' }, { fillStyle:'#558822' }],
                        // blue endpoints 7 px; green endpoints 11.
                        Endpoints : [ [ "Dot", {radius:7} ], [ "Dot", { radius:11 } ]],
                        // the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text; in this
                        // case it returns the 'labelText' member that we set on each connection in the 'init' method below.
                        ConnectionOverlays : [
                            [ "Arrow", { location:0.9 } ],
                            [ "Label", {
                                location:0.1,
                                id:"label",
                                cssClass:"aLabel"
                            }]
                        ],
                        ConnectorZIndex:5
                    });

                    // this is the paint style for the connecting lines..
                    var connectorPaintStyle = {
                                lineWidth:5,
                                strokeStyle:"#deea18",
                                joinstyle:"round",
                                outlineColor:"white",
                                outlineWidth:7
                            },
                    // .. and this is the hover style.
                            connectorHoverStyle = {
                                lineWidth:7,
                                strokeStyle:"#2e2aF8"
                            },
                    // the definition of source endpoints (the small blue ones)
                            sourceEndpoint = {
                                endpoint:"Dot",
                                paintStyle:{ fillStyle:"#225588",radius:7 },
                                isSource:true,
                                connector:[ "Flowchart", { stub:[40, 60], gap:10 } ],
                                connectorStyle:connectorPaintStyle,
                                hoverPaintStyle:connectorHoverStyle,
                                connectorHoverStyle:connectorHoverStyle,
                                dragOptions:{},
                                overlays:[
                                    [ "Label", {
                                        location:[0.5, 1.5],
                                        label:"Drag",
                                        cssClass:"endpointSourceLabel"
                                    } ]
                                ]
                            },
                    // a source endpoint that sits at BottomCenter
                    //	bottomSource = jsPlumb.extend( { anchor:"BottomCenter" }, sourceEndpoint),
                    // the definition of target endpoints (will appear when the user drags a connection)
                            targetEndpoint = {
                                endpoint:"Dot",
                                paintStyle:{ fillStyle:"#558822",radius:11 },
                                hoverPaintStyle:connectorHoverStyle,
                                maxConnections:-1,
                                dropOptions:{ hoverClass:"hover", activeClass:"active" },
                                isTarget:true,
                                overlays:[
                                    [ "Label", { location:[0.5, -0.5], label:"Drop", cssClass:"endpointTargetLabel" } ]
                                ]
                            },
                            init = function(connection) {
                                connection.getOverlay("label").setLabel(connection.sourceId.substring(6) + "-" + connection.targetId.substring(6));
                            };

                    var allSourceEndpoints = [], allTargetEndpoints = [];
                    _addEndpoints = function(toId, sourceAnchors, targetAnchors) {
                        for (var i = 0; i < sourceAnchors.length; i++) {
                            var sourceUUID = toId + sourceAnchors[i];
                            allSourceEndpoints.push(jsPlumb.addEndpoint(toId, sourceEndpoint, { anchor:sourceAnchors[i], uuid:sourceUUID }));
                        }
                        for (var j = 0; j < targetAnchors.length; j++) {
                            var targetUUID = toId + targetAnchors[j];
                            allTargetEndpoints.push(jsPlumb.addEndpoint(toId, targetEndpoint, { anchor:targetAnchors[j], uuid:targetUUID }));
                        }
                    };

                    _addEndpoints("window4", ["RightMiddle"], []);
                    _addEndpoints("window2", ["RightMiddle"], []);
                    _addEndpoints("window3", ["RightMiddle"], []);
                    _addEndpoints("window1", ["RightMiddle"], ["LeftMiddle"]);
                    _addEndpoints("window5", [], ["LeftMiddle"]);

                    // listen for new connections; initialise them the same way we initialise the connections at startup.
                    jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) {
                        init(connInfo.connection);
                    });

                    // make all the window divs draggable
                    //jsPlumb.draggable(jsPlumb.getSelector(".window"), { grid: [20, 20] });
                    // THIS DEMO ONLY USES getSelector FOR CONVENIENCE. Use your library's appropriate selector method!
                    jsPlumb.draggable(jsPlumb.getSelector(".window"));

                    //* connect a few up
                    jsPlumb.connect({uuids:["window2BottomCenter", "window3TopCenter"]});
                    jsPlumb.connect({uuids:["window2LeftMiddle", "window4LeftMiddle"]});
                    jsPlumb.connect({uuids:["window4TopCenter", "window4RightMiddle"]});
                    jsPlumb.connect({uuids:["window3RightMiddle", "window2RightMiddle"]});
                    jsPlumb.connect({uuids:["window4BottomCenter", "window1TopCenter"]});
                    jsPlumb.connect({uuids:["window3BottomCenter", "window1BottomCenter"]});

                    //
                    // listen for clicks on connections, and offer to delete connections on click.
                    //
                    jsPlumb.bind("click", function(conn, originalEvent) {
                        if (confirm("Delete connection from " + conn.sourceId + " to " + conn.targetId + "?"))
                            jsPlumb.detach(conn);
                    });

                    jsPlumb.bind("connectionDrag", function(connection) {
                        console.log("connection " + connection.id + " is being dragged");
                    });

                    jsPlumb.bind("connectionDragStop", function(connection) {
                        console.log("connection " + connection.id + " was dragged");
                    });
                }
            };
        })();

    </script>

    <script>
        /*
         *  This file contains the JS that handles the first init of each jQuery demonstration, and also switching
         *  between render modes.
         */
        jsPlumb.bind("ready", function() {

            // chrome fix.
            document.onselectstart = function () { return false; };

            // render mode
            var resetRenderMode = function(desiredMode) {
                var newMode = jsPlumb.setRenderMode(desiredMode);
                $(".rmode").removeClass("selected");
                $(".rmode[mode='" + newMode + "']").addClass("selected");

                $(".rmode[mode='canvas']").attr("disabled", !jsPlumb.isCanvasAvailable());
                $(".rmode[mode='svg']").attr("disabled", !jsPlumb.isSVGAvailable());
                $(".rmode[mode='vml']").attr("disabled", !jsPlumb.isVMLAvailable());

                //var disableList = (newMode === jsPlumb.VML) ? ",.rmode[mode='svg']" : ".rmode[mode='vml']";
                //	$(disableList).attr("disabled", true);
                jsPlumbDemo.init();
            };

            $(".rmode").bind("click", function() {
                var desiredMode = $(this).attr("mode");
                if (jsPlumbDemo.reset) jsPlumbDemo.reset();
                jsPlumb.reset();
                resetRenderMode(desiredMode);
            });

            // explanation div is draggable
            $("#explanation,.renderMode").draggable();

            resetRenderMode(jsPlumb.SVG);

        });
    </script>
</body>
</html>