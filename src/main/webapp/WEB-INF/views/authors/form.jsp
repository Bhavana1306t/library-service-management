<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${formTitle} — Library Management</title>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="navbar-brand">
        <span class="icon">📚</span>
        <span class="brand-text">LibraryMS</span>
    </a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/authors" class="active">Authors</a>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <h1>✍️ ${formTitle}</h1>
        <a href="${pageContext.request.contextPath}/authors" class="btn btn-secondary">← Back to Authors</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">❌ ${errorMessage}</div>
    </c:if>

    <div class="card">
        <div class="card-header">${formTitle}</div>
        <div class="card-body">

            <c:choose>
                <%-- ========== ADD FORM ========== --%>
                <c:when test="${author.id == null}">
                    <form:form action="${pageContext.request.contextPath}/authors/save"
                               method="post" modelAttribute="author">

                        <div class="form-row">
                            <div class="form-group">
                                <label>Full Name <span class="req">*</span></label>
                                <form:input path="name" cssClass="form-control"
                                            placeholder="e.g. Ernest Hemingway"/>
                                <form:errors path="name" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>Nationality <span class="req">*</span></label>
                                <form:input path="nationality" cssClass="form-control"
                                            placeholder="e.g. American"/>
                                <form:errors path="nationality" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Biography</label>
                            <form:textarea path="biography" cssClass="form-control"
                                          placeholder="Brief biography of the author…"/>
                            <form:errors path="biography" cssClass="invalid-feedback"/>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-success">💾 Save Author</button>
                            <a href="${pageContext.request.contextPath}/authors" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form:form>
                </c:when>

                <%-- ========== EDIT FORM ========== --%>
                <c:otherwise>
                    <form:form action="${pageContext.request.contextPath}/authors/update/${author.id}"
                               method="post" modelAttribute="author">

                        <div class="form-row">
                            <div class="form-group">
                                <label>Full Name <span class="req">*</span></label>
                                <form:input path="name" cssClass="form-control"/>
                                <form:errors path="name" cssClass="invalid-feedback"/>
                            </div>
                            <div class="form-group">
                                <label>Nationality <span class="req">*</span></label>
                                <form:input path="nationality" cssClass="form-control"/>
                                <form:errors path="nationality" cssClass="invalid-feedback"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Biography</label>
                            <form:textarea path="biography" cssClass="form-control"/>
                            <form:errors path="biography" cssClass="invalid-feedback"/>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-warning">✏️ Update Author</button>
                            <a href="${pageContext.request.contextPath}/authors" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form:form>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>
</body>
</html>
