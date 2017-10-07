$('.delete_student').click(function(){
    return confirm("Are you sure you want to delete?");
})

$(".getAvg").click(function(){
    url = $(this).attr("data-url")
    $.ajax({
        url: url,
        method: 'GET',
        success: function(data) {
            alert("The average is: " + data.average + "%");
        },
        error: function(xhr, ajaxOptions, thrownError) {
            alert(thrownError);
        }
    });
})
