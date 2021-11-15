<#setting url_escaping_charset='ISO-8859-1'>

<#function icon name type >
    <#switch type>
        <#case "documento">
            <#switch (name?keep_after_last('.')?lower_case) >
                <#case "doc">
                <#case "docx">
                    <#return "file-word">
                <#case "pdf">
                    <#return "file-pdf">
                <#default>
                    <#return "file">
            </#switch>
        <#default>
            <#return "folder">
    </#switch>
</#function>

<#function si num>
    <#assign order     = num?round?c?length />
    <#assign thousands = ((order - 1) / 3)?floor />
    <#if (thousands < 0)><#assign thousands = 0 /></#if>
    <#assign siMap = [ {"factor": 1, "unit": ""}, {"factor": 1000, "unit": "K"}, {"factor": 1000000, "unit": "M"}, {"factor": 1000000000, "unit":"G"}, {"factor": 1000000000000, "unit": "T"} ]/>
    <#assign siStr = (num / (siMap[thousands].factor))?string("0.#") + siMap[thousands].unit />
    <#return siStr />
</#function>

<style>

    .row-striped:nth-of-type(odd){
        background-color: #efefef;
    }

    .row-striped:nth-of-type(even){
        background-color: #ffffff;
    }

    .row-striped{
        padding:5px;
    }

    .row-item{
        padding-top:3px;
		text-align:left;
    }

</style>


<#assign object = buffers.object!"" />
<#assign records = buffers.related!records />


<#if (object?is_hash)>

<!--
<#list object?keys as key >
<#if key!="ftl" && key!="draft" >

	${key}=
	<#attempt>
		${ ((object[key])!"")?html}
	<#recover>
	</#attempt>
	\n
</#if>
</#list>-->

    <#assign pathSegments = utils.getBreadCrumb(object.VIRTUAL_PATH,object.PARENTIDS) />

<div class="well small">

	<span class="row">
	<strong style="font-size:large">
	<i class="fticon fticon-${icon(object.name,object.type)}"></i>
    ${object.display_name!object.name}
        <#if object.content_size?? >
            (${si( object.content_size?c?number)}B)
        </#if>
	</strong>
	</span>
    <span class="row col-sm-12">
    ${object.TYPE_ID!object.type}
	</span>
    <#if (pathSegments?? && (pathSegments?size)>0) >
        <div class="row col-sm-12">
            <#list pathSegments as part><a href="?type=${part.type}&sid=${part.sid}${querystringParams}">${part.name}</a>/</#list>
        </div>
        <br/>
    </#if>
    <br/>

    <div class="row">
        <!--<span class="col-sm-2">Tipo<br><strong>${object.type}</strong></span>-->

        <span class="col-sm-2">Creato il <br><strong>${utils.datetime(object.CREATED!"")}</strong> da <strong>${utils.getDisplayName(object.CREATOR!"",'user')}</strong></span>
        <span class="col-sm-2">Modificato il <br><strong>${utils.datetime(object.modified_on)!""}</strong> di <strong>${utils.getDisplayName(object.MODIFIER!"")!""}</strong></span>
        <!--<p class="item">Percorso<br><strong>${path!""}</strong></p>-->

        <#if (object.type=="documento")>

            <span class="col-sm-2 ellipsis">Mittente<br><strong title="${utils.getCard(object.MITTENTI)!""}" >${utils.getCard(object.MITTENTI)!""}</strong></span>
            <span class="col-sm-2 ellipsis">Destinatari
                <#list utils.getCards(object.DESTINATARI)![] as dest>
                    <br>
			<strong title="${dest}" >${dest}</strong>
                </#list>
	     </span>
        </#if>

    </div>
</div>
<#else>
</#if>

<#if (sortSpecs?size>0) >
<div class="row">
    <span class="col-sm-10" ><a class="order btn btn-link" href="?orderBy=${sortSpecs.name}${querystringParams}<#if (object?is_hash) >&sid=${object.sid}</#if>">NOME<i class="glyphicon"></i></a></span>
    <span class="col-sm-2"  >

		<button class="btn btn-link dropdown-toggle pull-right" type="button" id="dropdownMenu1" data-toggle="dropdown">
			Ordinamento&nbsp;
			<span class="glyphicon glyphicon-triangle-bottom"></span>
		</button>
		<ul class="dropdown-menu col-sm-12"  >
            <#list sortSpecs?keys as col >
                <#if (col?is_string && (sortSpecs[col]?has_content )) >
                    <li>
				<a class="cleanurl btn btn-link" href="?orderBy=${sortSpecs[col]}${querystringParams}<#if (object?is_hash) >&sid=${object.sid}</#if>">${col}</a>
			</li>
                </#if>
            </#list>
		</ul>
	</span>

</div>
</#if>

<div id="list-body" style="min-height:250px" >
<#list records as item>
    <div class="row-striped">

        <div class="row">
		<span class="col-sm-7" >

			<a class="btn btn-link row-item ellipsis" title="${item.name}" href="${context}/viewContent?type=${item.type}&sid=${item.sid}">
				<i class="fticon fticon-${icon(item.name,item.type)}"></i>&nbsp;${item.name}
			</a>

		</span>

		<span class="col-sm-3">
			<#if (item.type=="documento")>
			<span class="col-sm-5">
				<span class="small badge row-item ellipsis">
				${utils.getMessage("label.TYPE_ID."+item.TYPE_ID,item.TYPE_ID)}
				</span>
			</span>
			
			<span class="col-sm-5">
				<span class="small badge row-item ellipsis">
					${item.TIPO_COMPONENTE!""}
				</span>
			</span>

			<span class="col-sm-2">
				<a class="row-item print-hide pull-right" onclick="javascript:return closePreview();" href="downloadDoc?docNum=966676?sede=DOCAREA&amp;docNum=${item.sid!""}">
					<i title="scarica" class="glyphicon glyphicon-download"></i>
				</a>
			</span>
			</#if>
		</span>

		<span class="col-sm-2 small" style="text-align:right" >
			${utils.datetime(item.modified_on)}<br/>${utils.getDisplayName(item.modified_by)}
		</span>
        
		</div>

        <#if (item.NUM_PG??)>
            <div class="row small">
		<span class="col-sm-12" >
			Documento protocollato in ${item.TIPO_PROTOCOLLAZIONE!""} <strong>#${item.NUM_PG}</strong> del <strong>${utils.datetime(item.DATA_PG)}</strong> da <strong>${utils.getDisplayName(item.OPERATORE_DI_PROTOCOLLO!'')}</strong>
			<span class="ellipsis">
				<i class="muted">${item.OGGETTO_PG}</i>
			</span>
		</span>
            </div>
        </#if>
        
		<#include  "tipologie/"+(item.TYPE_ID!"NOT_FOUND")+"-snippet.ftl" ignore_missing=true  />


        <#if ( !(object?has_content) && (item.VIRTUAL_PATH?length>pathIdx) ) >
            <div class="row">
                <span class="col-sm-12" ><i class="small ellipsis" >${ item.VIRTUAL_PATH[pathIdx..(item.VIRTUAL_PATH?length-item.name?length-2)]}</i></span>
            </div>
        </#if>
    </div>
</#list>
</div>



<!--<div id="previewbox" class="previewbox">
	<button class="btn btn-warning btn-small pull-right" onclick="javascript:closePreview();" title="Chiudi"><i class="glyphicon  glyphicon-remove"></i></button>
	<div id="preview-content">Caricamento...</div>
</div>-->