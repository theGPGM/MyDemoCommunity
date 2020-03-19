/***
 * 提交回复
 */

function post() {

    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2Target(questionId, 1, content);
}

function comment2Target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容哦~~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();

            } else {
                alert(response.message);
            }
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function comment(e) {

    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2Target(commentId, 2, content);
}

/***
 *展开二级评论
 */
function collapseComment(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    console.log(comments);
    //获取二级评论状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        //折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            e.classList.add("active");
            e.setAttribute("data-collapse", "in");

        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    //media-left
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-avatar",
                        "src": comment.user.avatarUrl
                    }));

                    //media-body
                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body commentator",
                    }).append($("<span/>", {
                        "class": "commentator-name",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "class": "sub-menu"
                    }).append($("<span/>", {
                        "class": "comment-content",
                        "html": comment.content
                    })).append($("<span/>", {
                        "class": "pull-right publish-time",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));


                    //media
                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    //comment
                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });

                comments.addClass("in");
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}