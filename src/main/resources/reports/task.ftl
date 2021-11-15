

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
    }

    div.disabled {
        pointer-events: none;
        opacity: 0.5;
    }

</style>

<#assign object = buffers.object />

<!--
<#list object?keys as key >
<#if key!="ftl" && key!="draft" >
	${key}=${object[key]!""}\n
</#if>
</#list>

<#list buffers.pins?keys as key >
<#if key!="ftl" && key!="draft" >
	${key}=...\n
</#if>
</#list>

<!-- ActorId : ${buffers.pins.ActorId!""} -->
<!-- GroupId : ${buffers.pins.GroupId!""} -->
<!-- potOwners : ${object.potOwners!""} -->

<!--
		isStakeholder
		isAdmin
		canSkip
		canClaim
		canRelease

		canForward
		canDelegate
		canRefuse
		RefuseGroupId

		canEdit
-->

<#assign isActive = ( object.status == "Ready" || object.status == "Reserved" || object.status == "InProgress" || object.status == "Suspended" || object.status == "Completed" )  />

<#assign potOwners = (object.potOwners!"SYS_ADMINS")?split(";") />
<#assign stakeHolders = (buffers.pins.TaskStakeholderGroupId!"SYS_ADMINS")?split(";") />
<#assign BAs = (buffers.pins.BusinessAdministratorGroupId!"SYS_ADMINS")?split(";") />

<#assign RefuseGroupId = buffers.pins.RefuseGroupId!"" />

<#assign skippable = (buffers.pins.skippable!"false" == "true") />

<#assign isClaimed = (object.actualOwner?has_content) />
<#assign isAdmin = utils.getUserInfo().isAdmin() || utils.getUserInfo().hasGroup(BAs) />
<#assign isStakeholder = (utils.getUserInfo().hasGroup(stakeHolders) || isAdmin)  />
<#assign isActualOwner = ( (object.actualOwner!"") == utils.getUserInfo().getUsername()) />
<#assign isPotOwner = (utils.getUserInfo().hasGroup(potOwners) || isActualOwner || isStakeholder ) />

<#assign canClaim = !isClaimed && (isActive && ( object.status == "Ready" || object.status == "Reserved" ) && isPotOwner) />
<#assign canEdit = isActive && isActualOwner />
<#assign canRelease = isClaimed && isActive && (isActualOwner || isStakeholder) />
<#assign canForward = isActive && isStakeholder />
<#assign canDelegate = isActive && isStakeholder />
<#assign canRefuse = isActive && (RefuseGroupId?has_content) />
<#assign canSkip = isActive && skippable && isActualOwner />
<#assign forwardpanel = (isStakeholder||canRefuse) />


<div class="row well small">

	<span class="row">
	<span class="col-sm-10">
	<strong style="font-size:large">
	<i class="glyphicon glyphicon-forward"></i>
    ${object.name!""}
	</strong>
	</span>
	<span class="col-sm-2">
		<span class="pull-right" >
		#${object.id?c}
        <#if (utils.getUserInfo().isAdmin()) >
            <br/>
		<a title="workItem" href="taskWorkItemDetails?workItemId=${object.workItemId?c}&processInstanceId=${object.instanceId?c}">${object.workItemId?c}</a>
        </#if>

		</span>
	</span>
	</span>
    <br/>

    <div class="row col-sm-12">

        <a id="task-take" href="claimTask?id=${object.id}&amp;target=" class="btn btn-default ${canClaim?string('','disabled')}" title="Prendi in carico il task" role="button">
            <i class="glyphicon glyphicon-arrow-down"></i>
        </a>

        <a id="task-release" href="releaseTask?id=${object.id}&amp;target=" class="btn btn-default ${canRelease?string('','disabled')}" title="Rilascia il task" role="button">
            <i class="glyphicon glyphicon-arrow-up"></i>
        </a>

        <a id="btn_refuse" href="#" onclick="ht_refuse('${RefuseGroupId}')" style="display:none" class="btn btn-default ${canRefuse?string('','disabled')}" title="Rifiuta" role="button">
            <i class="glyphicon glyphicon-thumbs-down"></i>
        </a>

        <a id="btn_forward" href="#" onclick="ht_forward()" style="display:none" class="btn btn-default ${canForward?string('','disabled')}" title="Inoltra il task" role="button">
            <i class="glyphicon glyphicon-transfer"></i>
        </a>

        <a id="btn_delegate" href="#" onclick="ht_delegate()" style="display:none" class="btn btn-default ${canDelegate?string('','disabled')}" title="Assegna il task ad utente" role="button">
            <i class="glyphicon glyphicon-user"></i>
        </a>

        <a id="task-skip" onclick="return confirm('Sei sicuro di saltare il task?')" style="${skippable?string('','display:none')}" href="skipTask?id=${object.id}" class="btn btn-default ${canSkip?string('','disabled')}" title="Salta il task" role="button">
            <i class="glyphicon glyphicon-remove"></i>
        </a>

        <a role="presentation" href="#" class="btn btn-default" title="Commenti" data-toggle="modal" data-target="#commentModal"><i class="glyphicon glyphicon-comment"></i></a>

        <a id="task-take" onclick="savedraft();" href="#" class="btn btn-default pull-right" title="Salva bozza" role="button">
            <i class="glyphicon glyphicon-floppy-disk"></i>
        </a>
    <#if isAdmin>
        <a id="task-take" href="getTaskPin?taskId=${object.id}" class="btn btn-default pull-right" title="Salva bozza" role="button">
            <i class="glyphicon glyphicon-pencil"></i>
        </a>
    </#if>



    </div>

    <div class="row col-sm-12" >&nbsp;</div>

    <div class="row">

        <span class="col-sm-2">Stato<br><strong>${utils.getMessage("label.task."+object.status)}</strong></span>


        <span class="col-sm-2">Data inizio<br><strong>${utils.datetime(object.createdOn!"")}</strong></span>

        <span class="col-sm-4">Istanza corrente<br><strong>
			<a title="${object.processId}" href="instanceDetail?id=${object.instanceId?c}"><span>${object.desInstance}</span></a>
		</strong></span>

        <span class="col-sm-4">
		<#if object.primaryInstanceId!=object.instanceId >
            Istanza principale<br><strong>
            <a title="${object.primaryProcessId}" href="instanceDetail?id=${object.primaryInstanceId?c}"><span>${object.primaryDesInstance}</span></a>
        </#if>
        </strong></span>



    </div>
    <br/>
    <div class="row">

		<span class="col-sm-2">Assegnazione<br>
        <#list utils.getDisplayNames(object.potOwners!"") as owner ><strong>${owner}</strong><#sep><br/></#sep></#list>
		</span>
        <span class="col-sm-2">Stakeholder<br>
        <#list utils.getDisplayNames(buffers.pins.TaskStakeholderGroupId!"admins") as owner ><strong>${owner}</strong><#sep><br/></#sep></#list>
		</span>

        <span class="col-sm-4">In carico<br><strong><#if (object.actualOwner??) >${utils.datetime(object.startDate)!""} da ${utils.getDisplayName(object.actualOwner!"")}</#if></strong></span>

        <span class="col-sm-2">Scadenza<br><strong>${utils.datetime(object.expirationTime)!"-"}</strong></span>

        <span class="col-sm-2">Completamento<br><strong>${utils.datetime(object.completedOn)!"-"}</strong></span>


        <!--<p class="item">Percorso<br><strong>${path!""}</strong></p>-->

    </div>

<#if ((records?size)>0) >
    <br/>
    <div class="row col-sm-12">
        Allegati
    </div>
    <div class="row">
        <#list records as item>

            <span class="col-sm-2" >
				<strong><a href="${context}/viewProfile?docNum=${item.DOCNUM}">${item.DOCNAME}</a></strong>
			</span>

        </#list>
    </div>
</#if>
</div>
</div>

<div id="wrapper-ftl" class="row ${canEdit?string('','disabled')}" style="border:1px solid lightgray" >
${object.draft!utils.ftl(object.ftl,buffers.pins)!"Non disponibile"}
</div>
<#if forwardpanel>
    <#include "forward-panel.ftl" />
</#if>


<!-- COMMENTI -->
<div class="modal fade" id="commentModal" style="z-index:100000" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" style="width:70%" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Commenti</h4>
            </div>
            <div class="modal-body col-sm-12">

                <div class="col-sm-12" >

                    <div class="row">
                        <form action="sendComment" method="post" id="commentform">
                            <input type="hidden" name="target" value=""/>
                            <input type="hidden" name="id" value="${object.id}"/>
                            <input type="hidden" name="userid" value="${utils.getUserInfo().getUsername()}"/>

                            <div class="form-group">
                                <textarea class="form-control" name="comment" id="comment" placeholder="Aggiungi commento" ></textarea>
                            </div>

                            <input class="btn btn-default btn-small pull-right" name="submit" type="submit" value="Invia"/>
                        </form>
                    </div>
                    <!-- Elenco commenti -->

                <#list buffers.comments as comment>

                    <#assign res= comment.description?matches(r"^(?:\[([^\:]*)(?:\:(.*))?\])?(.*)$","s") />

                    <div class="row">
					<span class="row col-sm-12" >
						<small>
							<strong>
                                <#if !((res?groups[1]!"")?has_content) >
                                ${utils.getMessage("label.task.commento")}
                                <#elseif (res?groups[2]!"")?has_content >
                                    <span title="${res?groups[2]}" >${utils.getMessage("label.task.rifiuto")}</span>
                                <#else>
                                ${utils.getMessage("label.task."+res?groups[1])}
                                </#if>
							</strong>
							&nbsp;il&nbsp;<strong>${utils.datetime(comment.createdOn)!""}</strong> da
							<strong>${utils.getDisplayName(comment.createdBy!"")}</strong>
                            <#if ( comment.createdBy == utils.getUserInfo().getUsername() && !(res?groups[1])?has_content  ) >
                                &nbsp;|&nbsp;<a href="deleteComment?id=${object.id}&commentId=${comment.id}&target=">Elimina</a>
                            </#if>
						</small>
                        </p>
					</span>
                        <#if (res?groups[3]!"")?has_content>
                            <pre class="col-sm-12 comment-body">${res?groups[3]!""}</pre>
                        </#if>
                    </div>
                </#list>

                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <!--button type="button" class="btn btn-default">Save changes</button-->
            </div>
        </div>
    </div>

</div>
<script>

    function savedraft(){

        var res = ht_savedraft();

        if (res){
            location.reload();
        } else {
            alert("non è stato possibile salvare lo stato");
        }

    }

    function init(){

        if ($("#forward-panel").length>0){
            $("#btn_forward").show();
            $("#btn_delegate").show();
            $("#btn_refuse").show();
        }

        var form = $( "#wrapper2>form" );

        $('<input>').attr({
            type: 'hidden',
            name: 'TID',
            value: '${object.id}'
        }).appendTo(form);

        if (form.length>0){

            form[0].submit2 = form[0].submit;

            form[0].submit = function (event){
                ht_savedraft();
                form[0].submit2();
            }
        }
    }

    init();



</script>



