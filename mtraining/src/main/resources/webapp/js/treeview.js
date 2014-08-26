/*
JsTree Lib Controller and JSON Parser
*/

//Global variables
var jArray = new Array();
var iterator = 0;
var SERVICE_ROOT = "";
var id_hashmap = new Array();

(function () {
    'use strict';
	var treeview = angular.module("mtraining.treeview", []);
	setTimeout(function () {
		populateTree(SERVICE_ROOT);
		initTree(location.href,true);
		$('#content-tabs li a').click(function () {
				populateTree(SERVICE_ROOT);
				initTree(this.href,true,"#");
			});

	}	,1000);
	

}());

function initTree (url) {
	//run tree rendering if on the proper tab
	if ( url.search("treeView") != -1) {
		setTimeout(function () {
			//create an instance when the DOM is ready
			$('#jstree').jstree({
				"plugins" : ["state", "dnd","search"],
				"core" : {
					'data' : jArray,
					//TO DO: Manage callbacks from tree
					'check_callback' : function (operation, node, node_parent, node_position) {
						//console.log(node);
						//console.log(arguments);
					}
				}
			});
			//bind to events triggered on the tree
			$('#jstree').on("changed.jstree", function (e, data) {
				//console.log(data.instance.get_node(data.selected));
				});
		},1000);
	}
}

//Get JSON from server and rewrite it to tree's JSON format
function populateTree(SERVICE_ROOT) {
	var jsonURI = SERVICE_ROOT + "/motech-platform-server/module/mtraining/web-api/all";
	$.getJSON(jsonURI,function (data) {fillJson(data,true,0,"#")});
};


function fillJson(data, init, level, par) {
	//if initialization
	if (init) {
		jArray = new Array();
		iterator = 0;
		jArray[iterator] = {
			"id" : 0,
			"text" : "mtrainingModule",
			"parent" : par,
			"icon" : "",
			"state" : {
					opened : true,
					disabled : false,
					selected : false
				},
			li_attr : {},
			a_attr : {},
			"level" : level
		}
		par = 0;
		iterator++;
		level++;
	}
	
	data.forEach(function(item){
			jArray[iterator] = {
				"id" : item.id,
				"text" : item.name,
				"parent" : par,
				"icon" : "",
				"state" : {
					opened : false,
					disabled : false,
					selected : false
				},
				li_attr : {},
				a_attr : {},
				"level" : level

			}

			//id_hashmap[iterator] = item.id;
			iterator++;
			var child_table = item.courses || item.chapters || item.lessons || [];
				if(!(child_table === 0)) {
					fillJson(child_table,false,level+1,item.id);
				};
			
	})
}