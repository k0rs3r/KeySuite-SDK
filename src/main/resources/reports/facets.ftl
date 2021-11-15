<style>

	.facets {
		margin: 0px 0px 0px -15px;
		text-align: left;
	}

	.facets .remove-all {
		display:inline;
	}

	.facets .remove-all .btn {
		margin-top: 0px
	}

	.facets .value {
		text-align: left;
	}

	.facets .option {
		display: inline-block;
		text-align: left;
	}

	.facets .count {
		font-weight: bold;
		color: blue;
		float:right;
		text-align:right;
	}

	.facets .remove {
		color: red !important;
		font-size: 12px;
	}

	.facets .btn {
		-webkit-border-radius: 5px;
		border-radius: 5px;
	}

	.inline {
		padding-top:6px;
	}

	.inline-name {
		margin-bottom: -6px;
	}

	.inline .current {
		background-color: darkcyan;
		border-color: darkcyan;
		color: white !important;
	}

	.inline .value {
		padding-bottom: 0px;
	}

	.inline .option {
		width: 65px;
		font-size: 12px;
		margin-top: -2px;
	}

	.inline .count {
		width: 35px;
		margin-top: -3px;
		font-size: 14px;
	}

	.inline .current .count {
		color: red;
	}

	.combo {
		display:inline-block;
		margin-left:-12px;
	}

	.combo.facet {
		vertical-align: top;
	}

	.combo .option {
		width: 100px;
	}

	.combo .count {
		margin-top: -3px;
	}

	.combo .info {
		vertical-align:top;
		font-size: xx-small;
	}

	.combo .current {
		padding: 0px;
		margin-top: 0px;
	}

	.combo .first .option, .combo .name {
		border-bottom: 1px solid lightgray;
		border-radius: 0px;
	}

	.combo .next {
		display:block;
		margin-top:-10px;
		margin-bottom:0px;
		margin-left: -17px;
	}

	/*.combo .selected {
		padding-right: 8px;
		min-width: 0px;
	}*/

	.combo .selected .triangle {
		margin-top: -2px;
	}


	.combo .selected .count {
		color:red;
	}

	.combo .unselected .name {
		width: 120px;
		text-align: left;
		display: inline-block;
	}

	.combo .value {
		padding: 6px 20px 0px 5px;
		margin: -4px 2px -4px 2px;
	}

	.combo .first .remove {
		vertical-align: super;
	}

	.combo .next .remove {
		vertical-align: super;
	}

	.combo .value .option {
		font-size: 12px;
		margin-top: -2px;
		width: 200px;
	}

	.combo .dropdown-menu {
		min-width:300px;
	}

	.combo .value.selected .count {
		margin-right: -16px;
	}

	.combo .triangle {
		vertical-align: top;
	}


</style>

<span class="row facets">

<#list facets as facet>

	<#assign vals = lreq[facet]![] />
	<#assign prefix = "facet."+facet+"."+(facet==businessState)?string(processName!''+'.','') />

	<div class="btn-link inline-name" >$[facet.${facet}.label:${facet?lower_case?capitalize?replace('_',' ')}]</div>
	<div class="inline facet btn-group" role="group" >

	<#list counts[facet] as key,value >


		<#assign message = (params[prefix+key])!(utils.getDisplayName(key)) />

		<#if ( vals?seq_contains(key) ) >
			<a title='${message}' class="value current cleanurl btn btn-default" href="${baseUrl}?${utils.remove(querystringParams,facet,key)}" >
			<span class="ellipsis option">${message}</span>
			<span class="count">${ (value!0)?c}</span>
		</a>
		<#else>
			<a title='${message}' class="value ${ (key == '...')?string('disabled','') } cleanurl btn btn-default" href="${baseUrl}?${facet}=${key}${querystringParams}" >
			<span class="ellipsis option">${message}</span>
			<span class="count">${ (value!0)?c}</span>
		</a>
		</#if>
	</#list>
	</div>
</#list>

	<#if (combos?size>0 || reset) >

		<div class="combo-container" >

	<#list combos as facet,multivalue >

		<#assign vals = lreq[facet]![] />

		<#assign prefix = "facet."+facet+"."+(facet==businessState)?string(processName!''+'.','') />

		<div class="combo facet dropdown">
			<#if (vals?size>0) >

				<#assign message = (params[prefix+vals?first])!(utils.getDisplayName(vals?first)) />

				<button class="selected btn btn-link dropdown-toggle" type="button" data-toggle="dropdown" >

			<a title="${message}" class="first current cleanurl btn btn-link" href="${baseUrl}?${ moreItems?string( utils.remove(querystringParams,facet), utils.remove(querystringParams,facet,vals?first) ) }" >
				<span class="ellipsis option" >${message}</span>
					<#if (!moreItems && vals?size>1) >
						<span class="info" title="${vals[1..]?join(', ')}" >(+${vals?size-1})</span>
					</#if>
				<i title="Rimuovi filtro" class="remove glyphicon glyphicon-remove"></i>
			</a>

				<#if (multivalue) >
					<span class="glyphicon triangle glyphicon-triangle-bottom"></span>
				</#if>
		</button>
				<#if (vals?size>1 && moreItems) >
				<#list 1..(vals?size-1) as idx >

					<#assign message = (params[prefix+vals[idx]])!(utils.getDisplayName(vals[idx])) />

					<a title="${message}" class="next current cleanurl btn btn-link" href="${baseUrl}?${utils.remove(querystringParams,facet,vals[idx])}" >
			<span class="ellipsis option" >${message}</span>
			<i title="Rimuovi filtro" class="remove glyphicon glyphicon-remove"></i>
		</a>
				</#list>
			</#if>
			<#else>



				<button title="$[facet.${facet}.label:${facet?lower_case?capitalize?replace('_',' ')}]" class="unselected btn btn-link dropdown-toggle" type="button" data-toggle="dropdown"  >
			<span class="ellipsis name" >$[facet.${facet}.label:${facet?lower_case?capitalize?replace('_',' ')}]</span>
			<span class="glyphicon triangle glyphicon-triangle-bottom"></span>
		</button>
			</#if>

			<#if ( multivalue || vals?size==0 ) >

				<ul class="dropdown-menu" >

			<#list counts[facet] as key,value >

				<#assign message = (params[prefix+key])!(utils.getDisplayName(key)) />

				<li title="${message}" >

				<#if ( vals?seq_contains(key) ) >
					<a class="value selected cleanurl btn btn-link" href="${baseUrl}?${utils.remove(querystringParams,facet,key)}" >
					<span class="ellipsis option">${message}</span>
					<span class="count">
						${ (value!0)?c }
						<i title="Rimuovi filtro" class="remove glyphicon glyphicon-remove"></i>
					</span>
				</a>
				<#else>
					<a class="value unselected ${ (key=='...')?string('disabled','') } cleanurl btn btn-link" href="${baseUrl}?${facet}=${key}${querystringParams}" >
					<span class="ellipsis option">${message}</span>
					<span class="count">${ (value!0)?c }</span>
				</a>
				</#if>
			</li>
			</#list>
		</ul>

			</#if>
	</div>

	</#list>

			<#if reset >
				<div class="remove-all">
		<i class="glyphicon glyphicon-option-vertical"></i>
		<a class="cleanurl btn btn-link" href="?">Rimuovi filtri</a>
	</div>
			</#if>

</div>
	</#if>

</span>

