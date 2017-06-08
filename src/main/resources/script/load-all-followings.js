var request = new XMLHttpRequest();
request.open('GET', '/graphql/query/?query_id=17874545323001329&id=3012529741&first=7500', false);
request.send(null);

//noinspection JSAnnotator
return request.responseText;
