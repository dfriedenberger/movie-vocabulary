$( document ).ready(function() {
	var sentenceSource = document.getElementById("sentence-template").innerHTML;
		var sentenceTemplate = Handlebars.compile(sentenceSource);
		
		var rowSource = document.getElementById("row-template").innerHTML;
		var rowTemplate = Handlebars.compile(rowSource);

        changeParagraph = function(anker)
        {
            $(anker).find('.save').show('slow');
        }

        save = function(anker)
        {
            var data = { 
                sentence : {
                    text: anker.find('h3').text() , 
                    annotations: []
                },
                translation: {} 
            };

            anker.find('tr').each(function(ix,tr)
            {
                var key = $(tr).find('.word').val().trim();
                data.sentence.annotations.push({
                    key : key,
                    level: $(tr).find('.level').val(),
                    type: $(tr).find('.type').val()
                });
                var translation = $(tr).find('.translation').val().trim()
                if(translation)
                {
                    data.translation[key] = translation;
                }
            });

            console.log(data);
            $.ajax({
                url: '../change/'+movie,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                dataType: 'json'
            }).done(function() {
                $(anker).find('.save').hide('slow');
            });

        }

        addControls = function(html)
        {
            html.find(".command").click(function(ev){
                ev.preventDefault();

                var cmd = $(this).data("command");
                var anker = $(this).closest(".paragraph");
                switch(cmd)
                {
                    case 'tolowercase':
                        var translation = $(this).closest("tr").find($(this).data("ref"));
                        var text = translation.val().toLowerCase();
                        translation.val(text);
                        changeParagraph(anker);
                        break;
                    case 'deleterow':
                        var target = $(this).closest("tr");
                        target.hide('slow', function(){ target.remove(); });
                        changeParagraph(anker);
                        break;
                    case 'addrow':
                        var row = $(rowTemplate({
                            word: "",
                            level : -1,
                            type : "",
                            translation: ""
                        }));
                        addControls(row);
                        row.hide();
                        anker.find("table tbody").append(row);
                        row.show('slow');
                        changeParagraph(anker);
                        break;
                    case 'save':
                        save(anker);
                        break;
                    default:
                        console.log("Unknown command "+cmd);
                        break;

                }
            });

            html.find("input").change(function(ev){
                var anker = $(this).closest(".paragraph");
                changeParagraph(anker);
            });

            html.find("select").change(function(ev){
                var anker = $(this).closest(".paragraph");
                changeParagraph(anker);
            });
        }

        $.get( "../info/"+movie, function( data ) {
            console.log(data);
            $("#paragraphs").text(data.paragraphs);
            $("#verbs").text(data.paragraphs);
            $("#nouns").text(data.nouns);
            $("#adjs").text(data.adjs);
            $("#advs").text(data.advs);
        });

        $.get( "../metadata/"+movie, function( data ) {
            console.log(data);
            $("#md-title").val(data.title);
            $("#md-image").val(data.image);
            $("#md-image-top").val(data.image_top);
            $("#md-link").val(data.link);
            $("#md-link-trailer").val(data.link_trailer);
            $("#md-link-amazon").val(data.link_amazon);

            $("#metadata").find(".command").click(function(ev){

                var data = {
                    title: $("#md-title").val(),
                    image: $("#md-image").val(),
                    image_top: $("#md-image-top").val(),
                    link: $("#md-link").val(),
                    link_trailer: $("#md-link-trailer").val(),
                    link_amazon: $("#md-link-amazon").val(),

                }
                console.log(data);
                $.ajax({
                    url: '../metadata/'+movie,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(data),
                    dataType: 'json'
                }).done(function() {
                    $("#metadata").find('.save').hide('slow');
                });
            });

            $("#metadata").find("input").change(function(ev){
                $("#metadata").find('.save').show('slow');
            });

        });

		$.get( "../vocabulary/"+movie+"?offset=0&limit=15", function( data ) {

			$.each( data, function( ix , value ) {
				  

				var sentence = value.sentence;
				var translation = value.translation;

				
				var rows = "";

				$.each( sentence.annotations, function( ix, annotation ) {
				  console.log( annotation );
				  rows += rowTemplate({
						word: annotation.key,
						level : annotation.level,
						type : annotation.type,
						translation: translation[annotation.key]
					});
				});

				var paragraph = $(sentenceTemplate({
                    text: sentence.text,
                    readingIndex: value.readingIndex,
					rows: rows
                }));
                
                addControls(paragraph);

				$("#vocabulary").append(paragraph);


            });
            

            
			
        });
        

});