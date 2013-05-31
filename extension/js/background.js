var tabAssestsLoaded = [];
var removejQueryCache = function(tabId){
	var pos = tabAssestsLoaded.indexOf(tabId);
	if (pos !== -1){
		tabAssestsLoaded.splice(pos, 1);
	}
}

chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab){
	var url = tab.url,
		filter = /http:\/\/www.nhaccuatui.com\/playlist\//i;
	removejQueryCache(tab.id);
	console.log(filter);
	console.log(url);
	if (filter.test(url)){
		console.log("b");
		chrome.pageAction.show(tab.id);
	}
});

/*
chrome.pageAction.onClicked.addListener(function(tab){
	if (tabAssestsLoaded.indexOf(tab.id) === -1){
		chrome.tabs.executeScript(tab.id, {
			file: "js/jquery.min.js"
		}, function(){
			chrome.tabs.executeScript(tab.id, {
				file: "js/extension_clicked.js"
			});
		});
		tabAssestsLoaded.push(tab.id);
	} else {
		chrome.tabs.executeScript(tab.id, {
			file: "js/extension_clicked.js"
		});
	}
});

chrome.tabs.onRemoved.addListener(function(tab){
	removejQueryCache(tab.id);
});

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse){
	if (request.icon){
		chrome.pageAction.setIcon({
			tabId: sender.tab.id,
			path: request.icon
		});
		sendResponse({});
	}
});
*/