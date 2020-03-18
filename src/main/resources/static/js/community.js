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
    var comment = $("#comment-" + id);

    //获取二级评论状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        //折叠二级评论
        comment.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        //展开二级评论
        comment.addClass("in");
        $.getJSON("/comment/" + id, function (data) {
            // var commentBody = $("comment-body-" + id);
            // var items = [];
            //
            // $.each(data.data, function (comment) {
            //     let c = $("<div/>", {
            //         "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12",
            //         html: comment.content
            //     });
            //     items.push("<li id='" + key + "'>" + val + "</li>");
            // });
            // commentBody.appendChild($("<div/>", {
            //     "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 collapse sub-comments",
            //     "id": "comment-" + id,
            //     html: items.join("")
            // }));

            //标记评论状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        });
    }
}