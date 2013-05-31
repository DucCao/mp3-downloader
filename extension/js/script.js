$(function(){
	var id = localStorage.getItem('id');
	var showSetting = function(){
		var id = localStorage.getItem('id');
		if (id){
			$('#input-id').val(id);
		}
		$('#input-id-form').show();
		$('#send-playlist-form').hide();
	}
	var showMainView = function(){
		$('#text-id').val(localStorage.getItem('id'));
		$('#send-playlist-form').show();
		$('#input-id-form').hide();
	}
	var successMessage = function(message){
		$("#message-content").text(message);
		$(".alert")
		.removeClass("alert-error")
		.addClass("alert-success")
		.show();
	}
	var failMessage = function(message){
		$("#message-content").text(message);
		$(".alert")
		.removeClass("alert-success")
		.addClass("alert-error")
		.show();
	}

	$('.alert').hide();
	$('.alert .close').bind("click", function(e) {
	    $(this).parent().hide();
	});

	var sendRequest = function(link){
		$('#loading').show();
		$('button').attr("disabled", "disabled");
		var id = localStorage.getItem('id'),
			url = "http://mp3-downloader.appspot.com/send-mp3/phoneId" + id;
		url += "&nctLink="
		$.ajax({
			url: "http://mp3-downloader.appspot.com/send-mp3",
			data: {phoneId:id, ntcLink: link},
			success: function(){
				successMessage("Send playlist successfully!");
			},
			error: function(){
				failMessage("Send playlist fail, please try again!");
			},
			complete: function(){
				$("#loading").hide();
				$('button').removeAttr("disabled");
			}
		});
	}

	if (id){
		showMainView();
	} else {
		showSetting();
	}

	$('#submit-id').click(function(){
		var id = $('#input-id').val().trim();
		if (id){
			localStorage.setItem('id', id);
			showMainView();
			console.log('b');
		} else {
			alert('Id can not be empty');
		}
	});
	$('#edit-id').click(function(){
		showSetting();
	});
	$('#send-playlist').click(function(){
		chrome.tabs.query({ currentWindow: true, active: true }, function (tabs) {
			var tab = tabs[0];
		  	sendRequest(tab.url);
		});
	});
});