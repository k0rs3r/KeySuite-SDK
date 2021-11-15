<#setting url_escaping_charset='ISO-8859-1'>

<style>

    .row-striped:nth-of-type(odd){
        background-color: #efefef;
    }

    .row-striped2:nth-of-type(odd){
        background-color: lightgoldenrodyellow;
    }

    .row-striped2:nth-of-type(even){
        background-color: yellow;
    }

    .row-striped:nth-of-type(even){
        background-color: #ffffff;
    }

    .row-striped{
        padding:5px;
    }

    .row-striped2{
        padding:5px;
    }

    .row-item{
        padding-top:3px;
    }

</style>


<#assign object = buffers.object />
<#assign records = buffers.related!records />
<#assign pathSegments = utils.getBreadCrumb(object.VIRTUAL_PATH,object.PARENTIDS) />

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

<#if (object??)>

<div style="overflow: visible" class="well small">

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
            <#list pathSegments as part><a href="?type=${part.type}&sid=${part.sid}">${part.name}</a>/</#list>
        </div>
        <br/>
    </#if>
    <br/>



    <div class="row col-sm-12" style="padding-top:10px;padding-bottom:10px" >

        <#if (utils.checkRights("modificaProfilo",object.user_rights))>
            <div class="btn-group">
                <a class="btn btn-default" href="#" onclick="viewDoc()" >
                    <i class="glyphicon glyphicon-download"></i>&nbsp;Apri
                </a>

                <button type="button" class="btn btn-default dropdown-toggle" style="min-width:0px;" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>

                <ul class="dropdown-menu">
                    <li><a target="_new" href="downloadDoc?docNum=${object.DOCNUM}">Scarica originale</a></li>
                    <li><a target="_new" href="convert?docnum=${object.DOCNUM}&amp;action=convert&amp;disposition=attachment">Scarica in pdf</a></li>
                </ul>
            </div>
        </#if>

        <#if (utils.checkRights("modificaProfilo",object.user_rights))>
            <div class="btn-group">
                <a class="btn btn-default" href="editProfile?docNum=${object.DOCNUM}" title="Modifica">
                    <i class="glyphicon glyphicon-pencil"></i>&nbsp;Modifica
                </a>

                <button type="button" class="btn btn-default dropdown-toggle" style="min-width:0px;" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>

                <ul class="dropdown-menu">
                    <li><a href="editProfile?docNum=${object.DOCNUM}">Modifica profilo</a></li>
                    <li><a href="newVersion?docNum=${object.DOCNUM}">Modifica contenuto</a></li>
                    <li><a href="editUD?docNum=${object.DOCNUM}">Modifica allegati</a></li>
                </ul>
            </div>
        </#if>

        <#if (utils.checkRights("modificaProfilo",object.user_rights))>
            <div class="btn-group">
                <a class="btn btn-default" href="#">
                    Azioni
                </a>

                <button type="button" class="btn btn-default dropdown-toggle" style="min-width:0px;" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>

                <ul class="dropdown-menu">
                    <li><a href="editClassifica?docNum=${object.DOCNUM}">Classificazione</a></li>
                    <li><a href="editFascicoli?docNum=${object.DOCNUM}">Fascicolazione</a></li>
                    <li><a href="editProtocollo?docNum=${object.DOCNUM}">Protocollazione</a></li>
                    <li><a href="#">Registrazione</a></li>
                    <li><a href="editAssegnazioni?docNum=${object.DOCNUM}">Assegnazioni</a></li>
                </ul>
            </div>
        </#if>



        <#if (utils.checkRights("modificaProfilo",object.user_rights))>
            <div class="btn-group pull-right">
                <a href="#securityModal" onclick="" class="btn btn-default" role="button" data-toggle="modal" title="Diritti">
                    <i class="glyphicon glyphicon-user"></i>
                </a>

                <button type="button" class="btn btn-default dropdown-toggle" style="min-width:0px;" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="caret"></span>
                    <span class="sr-only">Toggle Dropdown</span>
                </button>

                <ul class="dropdown-menu">
                    <li><a href="#historyModal" data-toggle="modal" >Cronologia</a></li>
                    <!--<li><a href="#versionModal" data-toggle="modal" >Versioni</a></li>-->
                    <#assign versions=utils.parseXml(object.VERSIONS)["versions/version"]?size />
                    <li><a href="versionsList?docNum=${object.DOCNUM}">
                        Versioni (${versions})
                    </a></li>
                </ul>
            </div>
        </#if>


    </div>

    <div class="row">
        <!--<span class="col-sm-2">Tipo<br><strong>${object.type}</strong></span>-->

        <span class="col-sm-2">Creato il <br><strong>${utils.datetime(object.CREATED!"")}</strong> da <strong>${utils.getDisplayName(object.CREATOR!"",'user')}</strong></span>
        <span class="col-sm-2">Modificato il <br><strong>${utils.datetime(object.modified_on)!""}</strong> da <strong>${utils.getDisplayName(object.MODIFIER!"")!""}</strong></span>
        <!--<p class="item">Percorso<br><strong>${path!""}</strong></p>-->




        <span class="col-sm-2 ellipsis">Mittente<br><strong title="${utils.getCard(object.MITTENTI)!""}" >${utils.getCard(object.MITTENTI)!""}</strong></span>
        <span class="col-sm-2 ellipsis">Destinatari
            <#list utils.getCards(object.DESTINATARI)![] as dest>
                <br>
			<strong title="${dest}" >${dest}</strong>
            </#list>
	     </span>


    </div>

    <#if (object.NUM_PG??)>
        <div class="row alert alert-info" style="margin:0px" >

            Documento protocollato in ${object.TIPO_PROTOCOLLAZIONE!""} <strong>#${object.NUM_PG}</strong> del <strong>${utils.datetime(object.DATA_PG)}</strong> da <strong>${utils.getDisplayName(object.OPERATORE_DI_PROTOCOLLO)}</strong>
            <span class="ellipsis">
				<i class="muted">${object.OGGETTO_PG}</i>
			</span>

        </div>
    </#if>

</div>
<#else>
</#if>

<#include ( (object.TYPE_ID!"") +"-TYPE_ID.ftl") ignore_missing=true  />

<div id="list-body" style="min-height:250px" >
<#list records as item>
    <div class="row-striped">

        <div class="row">
		<span class="col-sm-8" >

			<a class="btn btn-link row-item ellipsis" title="${item.name}" href="?${querystringParams}&sid=${item.sid}&type=${item.type}">
				<i class="fticon fticon-${icon(item.name,item.type)}"></i>&nbsp;${item.name}
			</a>

		</span>

            <span class="col-sm-2">
                <#if (item.type=="documento")>
                    <span class="col-sm-6">
			<span class="badge row-item ellipsis">
            ${utils.getMessage("label.TYPE_ID."+item.TYPE_ID,item.TYPE_ID)}
			</span>

			<i>${item.TIPO_COMPONENTE!""}</i>
			</span>


			<span class="col-sm-6 row-item">
			<a class="print-hide pull-right" onclick="javascript:return closePreview();" href="downloadDoc?docNum=966676?sede=DOCAREA&amp;docNum=${item.sid!""}">
				<i title="scarica" class="glyphicon glyphicon-download"></i>
			</a>
			</span>
                </#if>
		</span>

            <span class="col-sm-2 small" style="text-align:right" >${utils.datetime(item.modified_on)}<br/>${utils.getDisplayName(item.modified_by)}</span>
        </div>

        <#if (item.NUM_PG??)>
            <div class="row small">
		<span class="col-sm-12" >
			Documento protocollato in ${item.TIPO_PROTOCOLLAZIONE!""} <strong>#${item.NUM_PG}</strong> del <strong>${utils.datetime(item.DATA_PG)}</strong> da <strong>${utils.getDisplayName(item.OPERATORE_DI_PROTOCOLLO)}</strong>
			<span class="ellipsis">
				<i class="muted">${item.OGGETTO_PG}</i>
			</span>
		</span>
            </div>
        </#if>
        <#include ( (item.TYPE_ID!"") +"-TYPE_ID.ftl") ignore_missing=true  />


        <#if !(object??)>
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

<div class="modal fade in" id="securityModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none; padding-right: 25px;">
    <div class="modal-dialog" style="width:80%">
        <div class="modal-content">

            <div class="modal-header">
                    <span class="row col-sm-12">
					<span class="col-sm-9">
					<#assign effective=object.user_profiles[0] />
                        I tuoi diritti sono <strong>${utils.getMessage("label.rights."+effective,effective)}</strong>
					<br/>
                    <#if (object.acl_inherits)>
                        Questo oggetto è <strong>non riservato</strong> (ha solo sicurezza esplicita)
                    <#else>
                        Questo oggetto non è <strong>riservato</strong> (eredita sicurzza dai nodi ascendenti)

                    </#if>
					</span>
					<span class="pull-right">
					<#if effective=="fullAccess" >
                        <a class="btn btn-primary" href="aclList?nodeId=${object.id}" >Modifica diritti</a>
					&nbsp;
                    </#if>

                        <a class="btn btn-primary" href="aclList?nodeId=${object.id}" class="close" data-dismiss="modal" >Chiudi</a>
					</span>
					</span>

            </div>
            <div class="col-sm-12 small">


                <h4 class="row col-sm-12" >Diritti espliciti</h4>

                <span class="row col-sm-4">

                <#list (object.acl_explicit![]) as acl >
                    <span class="row-striped2 col-sm-12">
							<#assign parts = acl?split(":")>
                        <#assign actor = utils.getDisplayName(parts[0]?split("@")[0]) />
                        <#assign role = parts[1] />
                        <span class="col-sm-8">
							<i class="${ ((parts[0]?split("@")[1]!"user")=='user')?string('userGroup-USER','userGroup-GROUP')}"></i>
                        ${actor}</span>
							<span class="col-sm-4"><i>${utils.getMessage('label.rights.'+role,role)}</i></span>
						</span>
                </#list>
					</span>

            <#if (object.acl_inherits)>

                <h4 class="row col-sm-12">
                    <input type="checkbox" onchange="$(this).is(':checked') ? $('.inherited').show() : $('.inherited').hide() "  >&nbsp;Mostra i diritti ereditati</input>
                </h4>

                <!--<h4 class="row col-sm-12" >Diritti ereditati</h4>-->

                <#list object.acl_inherited?keys as key >
                    <#if (key!=object.id) >
                        <#assign acls = object.acl_inherited[key] />
                        <#assign name = utils.getDisplayName(key) />

                        <span class="col-sm-4 inherited" style="display:none">
						<strong class="row col-sm-12" >${name}</strong>
                            <#list acls as acl >
                                <span class="row row-striped col-sm-12">
							<#assign parts = acl?split(":")>
                                    <#assign actor = utils.getDisplayName(parts[0]?split("@")[0]) />
                                    <#assign role = parts[1] />
                                    <span class="col-sm-8">
								<i class="${( ((parts[0]?split("@")[1])!"user")=='user')?string('userGroup-USER','userGroup-GROUP')}"></i>
                                    ${actor}</span>
								<span class="col-sm-4"><i>${utils.getMessage('label.rights.'+role,role)}</i></span>
							</span>
                            </#list>
					 </span>

                    </#if>
                </#list>

            <#else>
                <h4 class="row col-sm-12">
                    I diritti non vengono ereditati
                </h4>
            </#if>

            </div>

            <div class="modal-footer">

            </div>

        </div>
    </div>
</div>

<div class="modal fade in" id="versionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none; padding-right: 25px;">
    <div class="modal-dialog" style="width:30%">
        <div class="modal-content"  >

            <div class="modal-header">
                    <span class="row col-sm-12">
					<span class="col-sm-9">
					<h4>Versioni</h4>
					</span>
					<span class="pull-right">
					<a class="btn btn-primary" href="aclList?nodeId=${object.id}" class="close" data-dismiss="modal" >Chiudi</a>
					</span>
					</span>

            </div>
            <div class="col-sm-12 small" style="max-height: 600px;overflow-y: auto;">

            <#assign versions=utils.parseXml(object.VERSIONS)["versions/version"] />

            <#list versions as version >
                <span class="col-sm-4">${utils.datetime(version['date']?string)}</span>
                <span class="col-sm-4">${utils.getDisplayName(version['userId']?string)}</span>
                <span class="col-sm-4">
					<a href="downloadVersion?docNum=1017097&version=${version?index+1}"><i class="glyphicon glyphicon-download"></i>&nbsp;Scarica</a>
				</span>

            </#list>

            </div>

            <div class="modal-footer">

            </div>

        </div>
    </div>
</div>

<div class="modal fade in" id="historyModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none; padding-right: 25px;">
    <div class="modal-dialog" style="width:60%">
        <div class="modal-content"  >

            <div class="modal-header">
                    <span class="row col-sm-12">
					<span class="col-sm-9">
					<h4>Cronologia</h4>
					</span>
					<span class="pull-right">
					<a class="btn btn-primary" href="aclList?nodeId=${object.id}" class="close" data-dismiss="modal" >Chiudi</a>
					</span>
					</span>

            </div>
            <div class="col-sm-12 small" style="max-height: 600px;overflow-y: auto;">


            <#list buffers.audit as item >
                <span class="row row-striped col-sm-12">
					<span class="col-sm-2">${utils.datetime(item.data)}</span>
					<span class="col-sm-2">${utils.getDisplayName(item.user!"-")}</span>
					<span class="col-sm-4">${utils.getMessage("label.trace.method."+item.methodName,item.methodName)}</span>
					<span class="col-sm-4">
					<#assign segnatura = utils.parseSegnatura(item.extraData) />
                        <#if (segnatura?keys?size>0) >
                            <strong>${ (segnatura.Mittenti[0].denominazione)!"" }</strong>
						<i class="glyphicon glyphicon-arrow-right"></i>
						<strong><#list segnatura.Destinatari![] as dest >${dest.denominazione}<#sep>,</#list></strong>
						<br/>
						<i>${ (segnatura.Oggetto)!"" }</i>
                        </#if>
					</span>
					</span>
            </#list>



            </div>

            <div class="modal-footer">

            </div>

        </div>
    </div>
</div>

<script type="text/javascript" src="javascript/openfile.js" ></script>

<script>

    var fileTypes = ",ODG,PPT,ODP,XLS,CSV,TSV,ODS,ODT,TXT,HTML,RTF,DOC,XSLX,DOCX,PPTX,HTM,".split(",");

    function viewDoc() {

        var filename = '${object.DOCNAME}';
        var ext = '${(object.DOCNAME?keep_after_last('.')?upper_case)}';
        var downloadUrl = "downloadDoc?docNum=${object.DOCNUM}&inline=";

        if (fileTypes.indexOf(ext)>=0){

            var pdfUrl = "convert?docnum=${object.DOCNUM}&action=convert";
            var htmlUrl = "openHtml/openhtml.html#../"+pdfUrl+"&extension=html";

            openDoc(filename, downloadUrl, pdfUrl, htmlUrl );
        } else {
            openDoc(filename, downloadUrl);
        }
    }

</script>