<#include "functions.ftl" />

<style>

	.form-control {
		height: 28px;
		padding: 2px 10px;
	}

	.token-input-input-token-facebook input{
		box-shadow: none !important;
		cursor: text  !important;
		height: 22px !important;
	}

	.token-input-token-facebook span {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 120px;
		display: inline-block;
		vertical-align: middle;
	}

	.ftl-view.disabled {
		color: lightgrey;
	}

	.ftl-view {
		padding-right:0px;
	}

	.order {
		white-space: nowrap;
	}

	.order .glyphicon-sort {
		font-size: xx-small;
		margin-left:3px;
	}

	.limited  {
		max-width:150px;
	}
</style>

<div style="margin-right: -30px;" class="print-hide" >
	<#if (altftls?size > 1) >
		<span class="row col-sm-12">
		<span class="pull-right">
		<#list altftls as altftl >
			<a title="Utilizza la vista ${altftl}" class="${ (altftl == ftl)?string('disabled','') } ftl-view ftl-${altftl} btn btn-link" href="${context}/query/${qt}?ftl=${altftl}">
				<i class="glyphicon glyphicon-list-alt"></i>&nbsp;<span>$[ftl.${altftl}.label:${altftl}]</span>
			</a>
		</#list>
		</span>
	</span>
	</#if>

	<span class="row">
		<h2 style="display:inline" >${title}</h2>

		<#if form?has_content >
			<a title="" style="padding-right: 30px;" class="pull-right btn-link" href="${context}/query/${qt}?form${utils.remove(utils.remove(querystringParams,'ftl'),'form-ftl')}&ftl=${req['form-ftl']!''}">
				$[label.query.form.back:Vai alla form di ricerca]
			</a>
		</#if>

	</span>

	<#if subtitle?? >
		<span class="row" >
		${subtitle!""}
	</span>
	</#if>

	<#include "facets.ftl" />

	<#if showStats >
		<span style="" class="row" >
		<span class="result-stats">
			<#if ((totResults!-1) > -1) >
				<span class="tot-results">
				<#if (totResults == 0) >
					Non ci sono risultati.
				<#elseif (totResults ==1) >
					1 risultato.
				<#else>
					${totResults} risultati.
				</#if>
				</span>
			</#if>
			<#if elapsed?? >
				<span class="print-hide" >(${elapsed}ms)</span>
			</#if>
		</span>

		<span class="pull-right print-hide">
			<a title="apri altra scheda in visualizzazione di stampa" target="_new" href="?wt=print${querystringParams}&pageSize=-1">
				<i class="glyphicon glyphicon glyphicon-print"></i>
			</a>
			&nbsp;
			<a title="download csv" download="${qt!'report'}_${.now?date?iso_utc}.csv" href="?wt=csv${querystringParams}&pageSize=-1">
				<i class="fticon fticon-file-excel"></i>
			</a>
		</span>
	</span>
	</#if>
</div>

<div style="margin-right: -30px;">

	<#if action?? >
		<form class="cleanurl" action="${context}/query/${action}" method="GET">

			<#include view+".ftl" />

			<input name="form-ftl" type="hidden" value="${ftl}" />

			<div class="col-sm-12">
				<br/>
				<button class="btn btn-primary pull-right" type="submit">Esegui&nbsp;</button>
			</div>

		</form>
	<#else>
		<#include view+".ftl" />
	</#if>


</div>

<#include "paginator.ftl" />

<script>


	$(document).ready( function() {

		$(".cleanurl, .order, .pagination > li > a").on("click", function(){
			var url = $(this).attr("href");

			url = url.replace(/(&_=[^&]*)/,'');
			url = url.replace(/([?&])[^=]+=[&$]/,'$1');
			url = url.replace(/(&+)/,'&');
			url = url.replace(/(&$)/,'');

			if (url=="?" && location.href.indexOf("?form")>0)
				url = "?form";

			url += "&" + (location.href.match("ftl=[^&]+")||[])[0];

			//$(this).attr("href",url);

			kdm.fragment.open(url,"page-content", true );

			return false;
		});

		$(".order").each( function() {
			var field = $(this).attr("href").match(/orderBy=([^:]+)/)[1];

			if (location.href.indexOf(field+":asc")>0)
				$(this).find("i").html("&nbsp;&#x0e155;");
			else if (location.href.indexOf(field+":desc")>0)
				$(this).find("i").html("&nbsp;&#x0e156;");
		});
	});

	$('.cleanurl').submit(function() {

		var url = $(this).attr("action") + "?" + $(this).serialize();

		url = url.replace(/[^&\?]+=(&|$)/g,'').replace(/(&$)/,'');

		//kdm.fragment.open(url,"page-content", true );
		location.href = url;

		return false;

	});

</script>