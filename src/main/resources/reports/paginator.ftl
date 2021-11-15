<#if (totPage > 1) >
<center>
	<ul class="pagination">
  		<#if (pageNumber == 1 ) >
			<li class="disabled"><a href="#">&laquo;</a></li>
		<#else>
			<li class="active"><a href="?pageNumber=${pageNumber-1}&orderBy=${orderBy}${querystringParams}" class="btn btn-mini ">&laquo;</a></li>
		</#if>
		
  		<#list 1..totPage as loop >
    		<#if (loop == pageNumber) >
				<li class="disabled"><a href="#" >${loop}</a></li>
			<#elseif ( loop < 11 || loop > (totPage-10) || loop > (pageNumber-5) && loop< (pageNumber+5))  >
				<li class="active"><a href="?pageNumber=${loop}&orderBy=${orderBy}${querystringParams}" class="btn btn-mini ">${loop}</a></li>
			<#else>
				<!--<li>&nbsp;</li>-->
			</#if>
		</#list>
		
		<#if (pageNumber == totPage) >
			<li class="disabled"><a href="#">&raquo;</a></li>
		<#else>
			<li class="active"><a href="?pageNumber=${pageNumber+1}&orderBy=${orderBy}${querystringParams}" class="btn btn-mini ">&raquo;</a></li>
		</#if>
	</ul>
</center>
</#if>