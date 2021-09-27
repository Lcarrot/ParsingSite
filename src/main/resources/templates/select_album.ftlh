<!doctype html>
<#import "spring.ftl" as spring />
<html lang="en">

<head>
    <#--    <@boot.importBootstrap />-->
    <#--    <@hf.headForHeaderAndFooter />-->
    <#--    <@form.headForInputForm />-->
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Выбрать альбом</title>
    <#--    <title><@spring.message 'ad' /></title>-->
</head>
<body>
<#list albums as node>
    <a href="<@spring.url '/vk/group/${node.group_id}/album/${node.id}/parse/'/>">
        <p>${node.name}</p>
        <p> ${node.description}</p>
        <br>
    </a>
</#list>
</body>
</html>