(function ($) {

    return {
        initBirthdaysReminder: function() {
            $.getJSON("/rest/private/birthdayreminder/birthdays", function(list){

                $.each(list, function(i, item){

                    var content = "";
                    content += "<li class='clearfix' id='"+item.identityId+"'>";

                    content += "<div class='peoplePicture pull-left'><div class='avatarXSmall'><a href='"+item.profileLink+"'><img src='"+item.avatar+"'></a></div></div>";
                    content += "<div class='peopleInfo'>";
                    content += "<div class='peopleName'><a href='"+item.profileLink+"' target='_self'>"+item.username+"</a></div>";
                    content += "<div class='peopleContainer clearfix'>";
                    content +="<div class='peopleDisplay'><div class='peoplePosition'>"+item.birthday+"</div></div>";
				          	content += "</div>";
				            content += "</div></li>";

                    $("#birthdaysContent").append(content);

                });
            });
        }
    };
})($);
