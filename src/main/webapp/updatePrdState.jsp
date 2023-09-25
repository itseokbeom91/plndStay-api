<%--
  Created by IntelliJ IDEA.
  User: DEV2
  Date: 2023-09-25 (025)
  Time: 13:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>11번가 상품 판매상태 일괄처리</title>
</head>
<body>
    <p>
  판매상태 <br>
  <input type="radio" name="state" value="restartdisplay" > 판매중
  <input type="radio" name="state" value="stopdisplay" checked> 판매중지
    </p>
        <br>상품번호<br>
    <textarea id="prdList" style="min-width: 200px; min-height: 300px"></textarea>
    <p>
        <input type="submit" id="btnSubmit" value="수정"><br><br>
        <h3>수정 결과</h3>
        <textarea id="result"style="min-width: 400px; min-height: 300px"></textarea>
    </p>
</body>
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<script>
    $("#btnSubmit").click(function () {
        let state = $('input[name=state]:checked').val();
        let prdlist = $('#prdList').val();
        prdlist = prdlist.split('\n');
        for (let i = 0 ; i < prdlist.length ; i++){
            display(state, prdlist.at(i));
        }
        // display(state)
    });

    const display = function (state, prdNo) {
        let stateKo = '';
        if (state == 'stopdisplay'){
            stateKo = '판매 중지';
        } else {
            stateKo = '판매 재개';
        }
        $.ajax({
            type : 'GET',
            url : '11st/updateDisplay',
            dataType: 'jsonp',
            jsonpCallback : 'cd24',
            data: {
                prdNo: prdNo,
                state: state
            },
            async: false,
            success : function (result) {
                console.table(result)
                console.log(result.code);
                if (result.code != '200'){
                    $('#result').append('상품번호 : '+prdNo + ' 판매상태 변경 실패 : \n' + result.result+'\n\n');
                } else {
                    $('#result').append('상품번호 : '+prdNo +' '+ stateKo + '로 변경\n\n\n');
                }

            }
        });
    }
</script>
</html>
