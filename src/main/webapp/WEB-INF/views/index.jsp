<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style type="text/css">
        .coupang_btn{
            color: palevioletred;
        }
    </style>

</head>
<body>
<a href="/hotelStory/callapi?strAccommID=1000251">callApi - HotelStory</a><br><br>

callAPI - SGG
<input type="text" class="ssg_call" value="13283">
<input type="button" class="ssg_call_btn" value="SSGBUTTON"><br><br>
<%--<a href="/SSG/getInfo?num=10665">getInfo - SSG</a><br>--%>
<%--getInfo <input type="text" class="ssg_info">--%>
<%--<input type="button" class="ssg_btn" value="SSGBUTTON">--%>
<%--<br><br>--%>

modify - SSG
<input type="text" class="ssg_modify" value="13283">
<input type="button" class="ssg_img_btn" value="IMG">
<input type="button" class="ssg_desc_btn" value="desc">
<input type="button" class="ssg_stock_btn" value="stock">
<input type="button" class="ssg_stop_btn" value="stop">
<br><br>

insert - SSG
<input type="text" class="ssg_insert">
<input type="button" class="ssg_insert_btn" value="SSGBUTTON"><br><br>
<br><br>

<%--<input type="text" id="cp_con_id"><br><br>--%>
<%--<input type="button" id="create_lodgings" class="coupang_btn" value="숙박상품 생성">--%>
<%--<input type="button" id="update_lodgings" class="coupang_btn" value="숙박상품 수정">--%>
<%--<input type="button" id="creUpdRoom" class="coupang_btn" value="룸 생성/수정">--%>
<%--<input type="button" id="get_lodgings" class="coupang_btn" value="숙박상품 정보 보기" onclick="location.href='/coupang/getLodgingInfo';">--%>
<%--<input type="button" id="delete_lodging" class="coupang_btn" value="숙박상품 삭제" onclick="location.href='/coupang/deleteLodging';">--%>
<%--<input type="button" id="update_Lodging_Rate" class="coupang_btn" value="숙박상품 요금 수정">--%>
<%--<br><br>--%>

<%--<input type="button" id="requestToken" class="eland_btn" value="이랜드 토큰 발급">--%>
<%--<input type="button" id="getOrder" class="eland_btn" value="이랜드 주문 가져오기">--%>
<input type="button" id="testBtn" value="ONDA test">
</body>
<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
    $(".ssg_call_btn").click(function(){
        let txt = $(".ssg_call").val();
        let omk = "SSG"
        location.href="/API/callapi?intNum="+txt + "&strOmk=" + omk;
    })

    $(".ssg_btn").click(function(){
        let txt = $(".ssg_info").val();
        let omk = "SSG"
        if(txt != ""){location.href="/API/getInfo?intNum="+txt + "&strOmk=" + omk;}
    })

    $(".ssg_img_btn").click(function(){
        let txt = $(".ssg_modify").val();
        let type = "img"
        if(txt != ""){location.href="/API/SSG/modify?intNum="+txt + "&strType=" + type + "&strOmk=SSG";}
    })

    $(".ssg_desc_btn").click(function(){
        let txt = $(".ssg_modify").val();
        let type = "desc"
        if(txt != ""){location.href="/API/SSG/modify?intNum="+txt + "&strType=" + type + "&strOmk=SSG";}
    })

    $(".ssg_stock_btn").click(function(){
        let txt = $(".ssg_modify").val();
        let type = "stock"
        if(txt != ""){location.href="/API/SSG/modify?intNum="+txt + "&strType=" + type + "&strOmk=SSG";}
    })

    $(".ssg_stop_btn").click(function(){
        let txt = $(".ssg_modify").val();
        let type = "stop"
        if(txt != ""){location.href="/API/SSG/modify?intNum="+txt + "&strType=" + type + "&strOmk=SSG";}
    })

    $(".ssg_insert_btn").click(function(){
        let txt = $(".ssg_insert").val();
        let omk = "SSG"
        if(txt != ""){location.href="/API/SSG/insert?intNum="+txt + "&strOmk=" + omk;}
    })


    // 쿠팡--------------------------------------------------------------------------
    $("#create_lodgings").click(function(){
        let txt = $("#cp_con_id").val();
        location.href="/coupang/createLodging?con_id="+ txt;
    })

    $("#update_lodgings").click(function(){
        let txt = $("#cp_con_id").val();
        location.href="/coupang/updateLodgings?con_id="+ txt;
    })

    $("#creUpdRoom").click(function(){
        let txt = $("#cp_con_id").val();
        location.href="/coupang/creUpdRoom?con_id="+ txt;
    })

    $("#update_Lodging_Rate").click(function(){
        let txt = $("#cp_con_id").val();
        // let pyong_idx = "8"
        location.href="/coupang/updateLodgingRate?con_id="+ txt;
    })


    // 이랜드--------------------------------------------------------------------------
    $("#requestToken").click(function(){
        location.href="/eland/auth/requestToken";
    })

    $("#getOrder").click(function(){
        location.href="/eland/order/getOrder";
    })

    $("#testBtn").click(function() {
        $.ajax({
            type :'GET',
            // cancelBooking   createBooking
            url: "/onda/booking/cancelBooking?intBookingID=9",
            contentType: "application/json; charset=utf-8",

            success : function(responseData){
                console.log(responseData)
                alert(responseData.statusCode);
                alert(responseData.message);
            },
            error : function(err){
                console.log(err)
            }
        });

    });

</script>

</html>