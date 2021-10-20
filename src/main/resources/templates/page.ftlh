<!doctype html>
<html lang="en">

<head>
    <#--    <@boot.importBootstrap />-->
    <#--    <@hf.headForHeaderAndFooter />-->
    <#--    <@form.headForInputForm />-->
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>моай</title>
    <#--    <title><@spring.message 'ad' /></title>-->
</head>
<body>
<form method="post">
    <label>
        <input name="url" placeholder="enter url">
    </label>
    <label>
        <select name="site">
        <#list services as node>
            <option value="${node}">${node}</option>
        </#list>
        </select>
    </label>
    <button type="submit"> Отправить</button>
</form>
<#list tasks as task>
   <p>url : ${task.url}</p>
    <br>
    <p>id альбома : ${task.album_id}</p>
    <p>количество обработанных страниц : ${task.count} из ${task.allPagesCount}</p>
    <br>
</#list>
</body>
</html>