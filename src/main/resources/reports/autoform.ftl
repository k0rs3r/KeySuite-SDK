<h3>${request.title!""}</h3>
<span class=""><i>${request.description!""}</i></span>

<form action="${context}/query/${qt}" method="GET" >

    <div class="row" >
    <#list parameters?keys as parameter>
        <div class="col-sm-3 form-group">
            <label>${parameter}</label>

            <#if parameters[parameter] == 'Date'>
                <input name="${parameter}" type='date' class="form-control" />
            <#elseif parameters[parameter] == 'Datetime'>
                <input name="${parameter}" type='datetime-local' class="form-control" />
            <#elseif parameters[parameter] == 'Double'>
                <input name="${parameter}" type='number' class="form-control" />
            <#elseif parameters[parameter] == 'Integer'>
                <input name="${parameter}" type='number' step="1" pattern="\d+" class="form-control" />
            <#else>
                <input name="${parameter}" type='text' class="form-control" />
            </#if>
        </div>
    </#list>

    </div>

    <span class="row">
	<button class="btn btn-primary" type="submit"  >Esegui&nbsp;</button>
</span>
</form>
