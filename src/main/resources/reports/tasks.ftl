<table class="table table100 table-striped table-condensed">
    <thead>
    <tr>
        <th><a class="order " href="?orderBy=${sortSpecs.taskId}${querystringParams}">id<i class="glyphicon ${ (orderBy?starts_with('taskId '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.attivita}${querystringParams}">Attivit&agrave;<i class="glyphicon ${ (orderBy?starts_with('attivita '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.istanza_principale}${querystringParams}">Istanza principale<i class="glyphicon ${ (orderBy?starts_with('istanza_principale '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.istanza_corrente}${querystringParams}">Istanza corrente<i class="glyphicon ${ (orderBy?starts_with('istanza_corrente '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.stato}${querystringParams}">Stato<i class="glyphicon ${ (orderBy?starts_with('stato '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.data_inizio}${querystringParams}">Data inizio<i class="glyphicon ${ (orderBy?starts_with('data_inizio '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.in_carico}${querystringParams}">In carico<i class="glyphicon ${ (orderBy?starts_with('in_carico '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.completamento}${querystringParams}">Completamento<i class="glyphicon ${ (orderBy?starts_with('completamento '))?string('glyphicon-sort','')}"></i></a></th>
        <th><a class="order" href="?orderBy=${sortSpecs.scadenza}${querystringParams}">Scadenza<i class="glyphicon ${ (orderBy?starts_with('scadenza '))?string('glyphicon-sort','')}"></i></a></th>
        <th><i class="glyphicon glyphicon-paperclip"></i></th>
    </tr>
    </thead>
    <tbody>
    <#list records as record>
    <tr>
        <td>${record.taskId?c}</td>
        <td><a class="limited ellipsis" title="${record.attivita!''}" href="/taskDetails?id=${record.taskId?c}">${record.attivita!""}</a></td>
        <td><a class="limited ellipsis" title="${record.istanza_principale!''}" href="/instanceDetail?id=${record.primaryInstanceId?c}">${record.istanza_principale!""}</a></td>
        <td><#if record.processInstanceId != record.primaryInstanceId ><a class="limited ellipsis" title="${record.istanza_corrente!''}" href="/instanceDetail?id=${record.processInstanceId?c}">${record.istanza_corrente!""}</a></#if></td>
        <td>$[facet.Stato.${record.stato}]</td>
        <td>${ utils.datetime(record.data_inizio!"")}</td>
        <td><span class="limited ellipsis" title="${ utils.getDisplayName(record.in_carico!"")!""}">${ utils.getDisplayName(record.in_carico!"")!""}</span></td>
        <td>${ utils.datetime(record.completamento!"") }</td>
        <td>${ utils.datetime(record.scadenza!"") }</td>
        <td><a href="${context}/viewProfile?docNum=${record.DOCNUM!""}">${recordDOCNAME!""}</a></td>
    </tr>
    </#list>
    </tbody>
</table>