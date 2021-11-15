<div class="panel panel-default" >

<div class="panel-body" >

    <div class="row">
	
		<div class="col-sm-6 form-group">
            <label>Contenuto</label>
			<input name="text" class="form-control" value="${req.text!''}" />
        </div>
		
		<div class="col-sm-6 form-group">
            <label>Titolo</label>
			<input name="name" class="form-control" value="${req.name!''}" />
        </div>

	</div>
	<div class="row">
		
		<div class="col-sm-3 form-group">
            <label>Data creazione da</label>
			<input name="created_on_from" type="date" value="${req.created_on_from!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="created_on_to" type="date" value="${req.created_on_to!''}" class="form-control">
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Data modifica da</label>
			<input name="modified_on_from" type="date" value="${req.modified_on_from!''}" class="form-control">
        </div>
	
		<div class="col-sm-3 form-group">
            <label>a</label>
			<input name="modified_on_to" type="date" value="${req.modified_on_to!''}" class="form-control">
        </div>
		
	</div>
	<div class="row">
		
		<div class="col-sm-3 form-group">
            <label>Creato da</label>
			<input name="CREATOR"  value="${req.CREATOR!''}" data-tokenLimit="1" class="auto-completion form-control" data-source="${context}/query/solr-auto-facet?field=CREATOR" />
        </div>
		
		<div class="col-sm-3 form-group">
            <label>Modificato da</label>
			<input name="MODIFIER"  value="${req.MODIFIER!''}" data-tokenLimit="1" class="auto-completion form-control" data-source="${context}/query/solr-auto-facet?field=MODIFIER" />
        </div>
		
	</div>
	
	
	
	
	
</div>

</div>