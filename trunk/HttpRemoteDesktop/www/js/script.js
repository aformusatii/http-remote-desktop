var eventSendTimeout = 100;
var lastEventTask = false;
var keyCodes = '';

$(function() {
	  $("#desktop").mousemove(function(e) {
	      $('.cx').html(e.pageX);
	      $('.cy').html(e.pageY);
	      registerEvent(e, 'mousemove');
	      return prevent(e);
	  }).mousedown(function(e) {
	      sendEvent(e, 'mousedown');
	      return prevent(e);
	  }).mouseup(function(e) {
	      sendEvent(e, 'mouseup');
	      return prevent(e);
	  }).click(function(e) {
	      return prevent(e);
	  }).dblclick(function(e) {
	      return prevent(e);
	  }).focus();
	  
	  $('*').keypress(function(e) {
		  var code = (e.keyCode ? e.keyCode : e.which);
		  if (!e.shiftKey && (code > 36) && (code < 41)) {
			  code -= 10;
		  }
		  keyCodes += code + ',';
		  registerEvent(e, 'keypress');
	      return prevent(e);
	  });
	  
      $(document).bind('contextmenu', function(e){
    	  return prevent(e);
      }); 
      
      $('.control').mouseover(function () {
    	 $(this).addClass('focus'); 
      }).mouseout(function () {
    	 $(this).removeClass('focus'); 
      });
      
      $(window).resize(windowResize);
      windowResize();
      
      $('#up').click(function (e) {
    	  ly -= 50;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#down').click(function (e) {
    	  ly += 50;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#left').click(function (e) {
    	  lx -= 50;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#right').click(function (e) {
    	  lx += 50;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#zoomIn').click(function (e) {
    	  zoom -= 10;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#zoomOut').click(function (e) {
    	  zoom += 10;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });
      $('#zoomDef').click(function (e) {
    	  lx = 0;
    	  ly = 0;
    	  zoom = 0;
    	  sendEvent({}, 'refresh');
    	  return prevent(e);
      });      
});

function windowResize() {
	  dWidth = $(window).width();
	  dHeight = $(window).height();
	  registerEvent({}, 'refresh');
}

function registerEvent(e, name) {
	if (lastEventTask) {
		clearTimeout(lastEventTask);
	}	
	lastEventTask = setTimeout(function() {sendEvent(e, name);}, eventSendTimeout);
}

function sendEvent(e, name) {
	if (lastEventTask) {
		clearTimeout(lastEventTask);
	}
	
	lx = (lx < 0) ? 0 : lx;
	ly = (ly < 0) ? 0 : ly;
	zoom = (zoom < 0) ? 0 : zoom;
	
	$('.me').html(name);
	$('.dw').html(dWidth);
	$('.dh').html(dHeight);
	$('.z').html(zoom);

	var xyParam = "";
	var whichParam = "";
	var keyCodesParam = "";

	if (((typeof e.pageX) != 'undefined') && ((typeof e.pageY) != 'undefined')) {
		var x = e.pageX;
		var y = e.pageY;
		xyParam = '&x=' + x + '&y=' + y;
	    $('.cx').html(x);
	    $('.cy').html(y);		
	}

	if ((typeof e.which) != 'undefined') {
		whichParam = '&which=' + e.which;
	}
	
	if (keyCodes != '') {
		keyCodesParam = '&keyCodes=' + keyCodes;
		keyCodes = '';
	}
	
    $('#desktop').attr('src', 
    		'getScreenshot.action?event=' + name 
    		+ xyParam
    		+ whichParam
    		+ keyCodesParam
    		+ '&r=' + Math.random()
    		+ '&dWidth=' + dWidth 
    		+ '&dHeight=' + dHeight
    		+ '&zoom=' + zoom
    		+ '&lx=' + lx 
    		+ '&ly=' + ly);
    $('#desktop').focus();
    
    if (name != 'refresh') {
    	registerEvent(e, 'refresh');
    }
}

function prevent(e) {
	e.preventDefault();
	return false;
}