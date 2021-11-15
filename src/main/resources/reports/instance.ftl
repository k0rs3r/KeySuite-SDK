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

    .row-striped:nth-of-type(even){
        background-color: #efefef;
    }

    .row-striped:nth-of-type(odd){
        background-color: #ffffff;
    }

    .row-striped{
        padding:5px;
    }

    .row-item{
        padding-top:3px;
    }


    .task.completed.other {
        display:none
    }

    .instance-current {
        background-color: yellow !important;
    }

    .instance-child {
        background-color: lightgoldenrodyellow !important;
    }

    .instance-parent {
        background-color: lightgoldenrodyellow !important;
    }

    .subinstance {
        display:none;
    }

    .unavailable {
        display:none;
    }

    .cluster {
        font-weight: bolder;
        display:block !important
    }

</style>


<#assign object = buffers.object />


<div class="row well ">

	<span class="row">

	<span class="col-sm-10">

		<strong style="font-size:large">
		<i class="glyphicon glyphicon-book"></i>
        ${object.desInstance!""}
		</strong>


		<form style="display:inline-block" action="setPreferredStatus" method="POST" >
			<input type="hidden" name="processId" value="${object.instanceId?c}" />
			<button class="btn btn-link" type="submit" >
				<i class="glyphicon glyphicon-star${ (object.preferred==1)?string('','-empty') }"> </i>
			</button>
		</form>

		<br/>
		<i>${object.processName}&nbsp;(${object.processVersion})</i>


	</span>

	<span class="col-sm-2">
		<span class="pull-right" >#${object.instanceId?c}</span>
	</span>

	</span>

    <div class="row col-sm-12" style="padding-top:10px;padding-bottom:10px" >
        <a class="btn btn-default" href="${context}/processPreview?id=${object.instanceId?c}" title="Preview">
            <i class="glyphicon glyphicon-eye-open"> </i>
        </a>

        <a class="btn btn-default" href="${context}/instanceHistory?id=${object.instanceId?c}&amp;pageSize=10" title="History">
            <i class="glyphicon glyphicon-th-list"> </i>
        </a>

        <a class="btn btn-default  ${(object.status>1)?string('disabled','')}" href="${context}/getInstanceVariableInit?id=${object.instanceId?c}" title="modifica variabili">
            <i class="glyphicon glyphicon-pencil"> </i>
        </a>

        <a class="btn btn-default ${(object.status>1)?string('disabled','')}"  data-toggle="modal" href="#complete_istance" title="Completa">
            <i class="glyphicon glyphicon-ok"> </i>
        </a>

    </div>


    <br/>

<#if object.parentInstanceId?? >
    <div class="row">

		<span class="col-sm-4">Istanza padre<br>
		<strong>
			<a title="${object.parentProcessId}" href="instanceDetail?id=${object.parentInstanceId?c}"><span>${object.parentDesInstance}</span></a>
		</strong>
		</span>

        <#if object.primaryInstanceId!=object.parentInstanceId >
            <span class="col-sm-4">Istanza principale<br>
		<strong>
			<a title="${object.primaryProcessId}" href="instanceDetail?id=${object.primaryInstanceId?c}"><span>${object.primaryDesInstance}</span></a>
		</strong>
		</span>
        </#if>

    </div>
    <br/>
</#if>

    <div class="row">

        <!--
		<#list object?keys as key >
			<#if key!="ftl">
			${key}=${object[key]!""}\n
			</#if>
		</#list>
		-->

        <span class="col-sm-2">Avvio<br><strong>${utils.datetime(object.start_date)!""}</strong> da <strong>${utils.getDisplayName(object.createdBy!"",'user')}</strong></span>
        <span class="col-sm-2">Stato<br><strong>${utils.getMessage("label.processStatus."+object.status)}</strong></span>
        <span class="col-sm-2">Business State<br><strong>${object.businessState!""}</strong></span>
        <span class="col-sm-2">Business Key<br><strong>${object.businessKey!object.instanceId?c}</strong></span>
        <span class="col-sm-2">Completamento<br><strong>${utils.datetime(object.end_date)!""}</strong></span>
        <span class="col-sm-2">ACL<br>
        <#list utils.getDisplayNames(buffers.security.actors) as actor ><strong>${actor}</strong><#sep>, </#sep></#list>
		</span>


    </div>
</div>

<div class="row">

    <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" onclick="updateHash('stato')"  href="#stato">Stato istanza</a></li>
        <li><a data-toggle="tab" onclick="updateHash('tasks')" href="#tasks" style="${ (object.status<2)?string('','display:none') }" >Attività in corso</a></li>
        <li><a data-toggle="tab" onclick="updateHash('completed')" href="#completed">Attività completate</a></li>
        <li><a data-toggle="tab" onclick="updateHash('instances')" href="#instances">Istanze collegate</a></li>
        <li><a data-toggle="tab" onclick="updateHash('variables')" href="#variables" style="${ (utils.getUserInfo().isAdmin() )?string('','display:none') }" >Variabili di processo</a></li>
        <li><a data-toggle="tab" onclick="updateHash('monitor')" href="#monitor" style="${ (object.status<2)?string('','display:none') }" >Monitoraggio</a></li>
    </ul>

    <div class="tab-content" style="overflow:hidden;border:1px solid lightgray;padding:5px">

        <div id="stato" class="tab-pane fade in active">

        <#if (object.ftl!"")?has_content >
        ${utils.ftl(object.ftl,buffers.variables)}
        <#else>
            Non disponibile
        </#if>
        </div>

        <div id="variables" class="tab-pane fade in active">

	<span class="col-sm-12">
		<input type="checkbox" onchange="$(this).is(':checked') ? $('.unavailable').show() : $('.unavailable').hide() "  >&nbsp;Mostra le variabili non disponibili</input>
	</span>

            <span class="col-sm-12">&nbsp;</span>
        <#list buffers.variables?keys as key>
            <#attempt>
                <span title="${key}" class="col-sm-2 ellipsis"><strong><a class="pull-right" href="getInstanceVariable?processInstanceId=${object.instanceId?c}&variableName=${key}">${key}</a></strong></span>
                <#assign value = buffers.variables[key] >

                <#if value?is_sequence>
                    <span title="<#list value as item>${item?html}<#sep>,</#list>" class="col-sm-2 ellipsis">&nbsp;
                        <#list value as item>${item?html}<#sep>,</#list>
		</span>
                <#else>
                    <span title="${value?html}" class="col-sm-2 ellipsis">&nbsp;
                    ${value?html}
		</span>
                </#if>

                <#recover>
                    <span title="${key}" class="col-sm-2 ellipsis unavailable"><strong><a class="pull-right" href="getInstanceVariable?processInstanceId=${object.instanceId?c}&variableName=${key}">${key}</a></strong></span>
                    <span class="col-sm-2 unavailable ellipsis">
		<i style="color:red" >-</i>
		</span>
            </#attempt>
        </#list>
        </div>

        <div id="tasks" class="tab-pane fade">

		<span class="col-sm-12">
			<input type="checkbox"  onchange="$(this).is(':checked') ? $('.task.other.completed').show() : $('.task.other.completed').hide() "  >&nbsp;Mostra attività di altri</input>
		</span>

            <span class="col-sm-12">&nbsp;</span>

        <#assign cnt = 0 />
            <div class="col-sm-12" id="tasklistOpened" >
                <div class="row" style="font-weight:bolder" >
                    <span class="col-sm-2">Attività</span>
                    <span class="col-sm-2">Istanza principale</span>
                    <span class="col-sm-2">Istanza corrente</span>
                    <span class="col-sm-1">Stato</span>
                    <span class="col-sm-2">In carico</span>
                    <span class="col-sm-1">Data inizio</span>
                    <span class="col-sm-1">Completamento</span>
                    <span class="col-sm-1">Scadenza</span>
                </div>
            <#list buffers.tasks as item>
                <#if (item.status=='Ready' || item.status=='InProgress') >
                    <div class="row row-striped">
				<span title="${item.name}" class="col-sm-2">
					<a href="taskDetails?id=${item.id?c}"><span>${item.name}</span></a>
				</span>
                        <span title="${item.primaryProcessId} ${item.primaryDesInstance}" class="col-sm-2 ellipsis">
					<a href="instanceDetail?id=${item.primaryInstanceId?c}"><span>${item.primaryDesInstance}</span></a>
				</span>
                        <span title="${item.processId} ${item.desInstance}" class="col-sm-2 ellipsis">
					<a href="instanceDetail?id=${item.instanceId?c}"><span>${item.desInstance}</span></a>
				</span>
                        <span class="col-sm-1">${utils.getMessage("label.task."+item.status) }</span>
                        <span class="col-sm-2"> <#if item.actualOwner?? > ${utils.datetime(item.startDate)!""} da ${utils.getDisplayName(item.actualOwner!"")!""} </#if></span>
                        <span title="${utils.datetime(item.createdOn)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.createdOn)!""}</span>
                        <span title="${utils.datetime(item.completedOn)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.completedOn)!""}</span>
                        <span title="${utils.datetime(item.expirationTime)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.expirationTime)!""}</span>
                    </div>
                    <#assign cnt = (cnt + 1) />
                </#if>
            </#list>
            <#if (cnt==0) >Non ci sono attività in corso</#if>
            </div>
        </div>

        <div id="completed" class="tab-pane fade">

		<span class="col-sm-12">
			<input type="checkbox"  onchange="$(this).is(':checked') ? $('.task.other.completed').show() : $('.task.other.completed').hide() "  >&nbsp;Mostra attività completate da altri</input>
		</span>

            <span class="col-sm-12">&nbsp;</span>

        <#assign cnt = 0 />
            <div class="col-sm-12" id="tasklistClosed" >

                <div class="row" style="font-weight:bolder" >
                    <span class="col-sm-2">Attività</span>
                    <span class="col-sm-2">Istanza principale</span>
                    <span class="col-sm-2">Istanza corrente</span>
                    <span class="col-sm-1">Stato</span>
                    <span class="col-sm-2">In carico</span>
                    <span class="col-sm-1">Data inizio</span>
                    <span class="col-sm-1">Completamento</span>
                    <span class="col-sm-1">Scadenza</span>
                </div>
            <#list buffers.tasks as item>
                <#if (item.status!='Ready' && item.status!='InProgress') >
                    <div class="row row-striped ${ ( (item.actualOwner!"")==utils.getUserInfo().getUsername() )?string('mine','other') } task ">
					<span title="${item.name}" class="col-sm-2">
						<a href="taskDetails?id=${item.id?c}"><span>${item.name}</span></a>
					</span>
                        <span title="${item.primaryProcessId} ${item.primaryDesInstance}" class="col-sm-2 ellipsis">
						<a href="instanceDetail?id=${item.primaryInstanceId?c}"><span>${item.primaryDesInstance}</span></a>
					</span>
                        <span title="${item.processId} ${item.desInstance}" class="col-sm-2 ellipsis">
						<a href="instanceDetail?id=${item.instanceId?c}"><span>${item.desInstance}</span></a>
					</span>
                        <span class="col-sm-1">${utils.getMessage("label.task."+item.status) }</span>
                        <span class="col-sm-2"> <#if item.actualOwner?? > ${utils.datetime(item.startDate)!""} da ${utils.getDisplayName(item.actualOwner!"")!""} </#if></span>
                        <span title="${utils.datetime(item.createdOn)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.createdOn)!""}</span>
                        <span title="${utils.datetime(item.completedOn)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.completedOn)!""}</span>
                        <span title="${utils.datetime(item.expirationTime)!""}" class="col-sm-1 ellipsis">${utils.datetime(item.expirationTime)!""}</span>
                    </div>
                    <#assign cnt = (cnt + 1) />
                </#if>
            </#list>
            <#if (cnt==0) >Non ci sono attività chiuse</#if>
            </div>
        </div>

        <div id="instances" class="tab-pane fade" >

		<span class="col-sm-12">
			<input type="checkbox"  onchange="$(this).is(':checked') ? $('.subinstance').show() : $('.subinstance').hide() "  >&nbsp;Mostra tutte le istanze del cluster</input>
		</span>

            <span class="col-sm-12">&nbsp;</span>

            <div class="subinstances col-sm-12" >
            <#if (buffers.processes?size>0) >

                <div class="row" style="font-weight:bolder" >
                    <span class="col-sm-3">Istanza</span>
                    <span class="col-sm-3">Processo</span>
                    <span class="col-sm-1">Stato</span>
                    <span class="col-sm-1">Business</span>
                    <span class="col-sm-2">Data Inizio</span>
                    <span class="col-sm-2">Data Fine</span>
                </div>

                <#assign cluster = (utils.clusterize(buffers.processes,object.instanceId)) />

                <#list cluster as item>
                    <#assign path = item.PATH />
                    <#assign parts = path?split('|') />

                    <div class="row row-striped subinstance ${ (item.CURRENT!false)?string('cluster instance-current','')} ${ (item.CHILD!false)?string('cluster instance-child','')} ${ (item.PARENT!false)?string('cluster instance-parent','')}" >

				<span style="padding-left:${parts?size}0px" class="col-sm-3 ellipsis" title="${item.processId} ${item.desInstance}" >
                    <#if (item.CURRENT!false) >
                    ${item.desInstance}
                    <#else>
                        <a href="instanceDetail?id=${item.instanceId?c}#instances"><span >${item.desInstance}</span></a>
                    </#if>
				</span>
                        <span class="col-sm-3 ellipsis">${item.processName}&nbsp;(${item.processVersion})</span>
                        <span class="col-sm-1">${utils.getMessage("label.processStatus."+item.status,""+item.status) }</span>
                        <span class="col-sm-1">${item.businessState!""}</span>
                        <span class="col-sm-2 ellipsis" title="${utils.datetime(item.start_date)!""}" >${utils.datetime(item.start_date)!""}</span>
                        <span class="col-sm-2 ellipsis" title="${utils.datetime(item.end_date)!""}" >${utils.datetime(item.end_date)!""}</span>

                    </div>

                </#list>

            <#else />
                Non ci sono istanze collegate
            </#if>
            </div>
        </div>

        <div id="monitor" class="tab-pane fade">

		<span class="col-sm-12">
			<input type="checkbox" checked onchange="$(this).is(':checked') ? $('.error-other').show() : $('.error-other').hide() "  >&nbsp;Mostra nodi di tutto il cluster</input>
		</span>

            <span class="col-sm-12">&nbsp;</span>

            <div class="col-sm-12">
            <#list buffers.errors as item>
                <div class="row" style="font-weight:bolder">
                    <span class="col-sm-3">Data</span>
                    <span class="col-sm-4">Istanza</span>
                    <span class="col-sm-5">WorkItem</span>
                </div>
                <div class="row row-striped ${ ( item.instanceId== (object.instanceId?c?string) )?string('error-current','error-other') }" >

                    <span class="col-sm-3">${utils.datetime(item.timestamp) }</span>
                    <span class="col-sm-4"><a href="instanceDetail?id=${item.instanceId}">${item.desInstance}</a></span>
                    <span class="col-sm-5"><a href="asyncWorkItemDetails?workItemId=${item.workItemId}&processInstanceId=${item.instanceId}" >${item.nodeName }</a></span>

                </div>
            </#list>
            </div>
        </div>

    </div>

</div>

</div>




<div class="modal fade in" id="complete_istance" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none; padding-right: 25px;">
    <div class="modal-dialog">
        <div class="modal-content">
            <form data-async="" data-target="#complete_istance" action="completeInstance" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title" id="myModalLabel">
                        Completa istanza di processo</h4>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="completeInstanceId" value="${object.instanceId?c}">
                    <label for="modal-complete-description">
                        Descrizione</label>
                    <textarea class="form-control" id="modal-complete-description" name="descriptionCompleteProcessIstance"></textarea>
                    <hr>
                    <div class="alert alert-warning">
                        Attenzione: il completamento dell’istanza è irreversibile.</div>
                    <div id="checkForceComplete" class="force-complete-check">
                        <label for="forceCompleteInput">
                            Forza Chiusura</label>
                        <input id="forceCompleteInput" type="checkbox" name="forceComplete">
                    </div>
                </div>

                <div class="modal-footer">
                    <button class="btn btn-primary" type="submit">
                        Completa</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>

    function init(){
        $('[href="'+window.location.hash+'"]').tab('show');
    }

    function updateHash(hash){
        //var url = "?processId=" + new URLSearchParams(window.location.search).get("processId")+hash;
        location.hash = "#"+hash.replace("#","");
        window.history.replaceState({path:location.hash},'',location.hash);
    }

    $(function() {
        init();
    });

</script>



