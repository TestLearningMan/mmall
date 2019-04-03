<html>
<body>
<h2>Hello World!</h2>
SpringMVC文件上传
<form action="/manate/product/upload.do" method="post">
    <input type="file" name="upload_file" formenctype="multipart/form-data"/>
    <input type="submit" value="springMVC上传文件"/>
</form>

富文本文件上传
<form action="/manate/product/richtext_img_upload.do" method="post">
    <input type="file" name="upload_file" formenctype="multipart/form-data"/>
    <input type="submit" value="富文本上传文件"/>
</form>

</body>
</html>
