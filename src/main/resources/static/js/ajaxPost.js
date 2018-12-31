$( document ).ready(function() {
	$('.btn.btn-lg.btn-primary.btn-block').click(function(){
		ajaxPost();
		event.preventDefault();
	});
	
	 function ajaxPost(){
		 $.ajax({
			  type: "GET",
			  url: "/send",
			  contentType: 'application/json',
			  data: { 
				  'email': $('#email').val(),
		          'money': $('#money').val(),
			  },
			  success: function(data) {
				  console.log(data);
				  var idx = 0; 
				  var key = Object.keys(data)[idx];
				  //var value = data[key]
				  $('.currentBalance').text('Your current balance is:' + key);
				$('#response').append('<h3>Money sent succesfully!</h3>');
			  },
			  error : function(e) {
					alert("Error!")
					console.log("ERROR: ", e);
				}
			});
	 }
})
