<#compress>
    <#list columns as item>${item}<#sep>,</#list>
    <#list data as record>
        <#list record as value><#if (value!"")?is_sequence >${value?size}<#elseif (value!"")?is_number >${value?c!""}<#else>${value!""}</#if><#sep>,</#list>
    </#list>
</#compress>