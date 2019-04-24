const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//

const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
var db = admin.database();

exports.simpleDbFunction = functions.database.ref('/ROOT/Comment/{commentId}')
    .onCreate((snap, context) => {
          var data = snap.val();
          var ref = db.ref("ROOT/USER/"+data.UserId);
          ref.once("value", function(snapshot) {
            const count = snapshot.val().CommentCount;
            if(count===5){
              ref = db.ref("ROOT/Comment");
              ref.orderByChild("UserId").equalTo(data.UserId).once("value", function(snapsho) {
                var min=0;
                var key;
                snapsho.forEach(userSnapshot => {
                    var k = userSnapshot.key;
                    var id = userSnapshot.val().TimeStamp;
                      if(min === 0){
                        min=id;
                        key=k;
                      }
                      if(min>id){
                        min=id;
                        key=k;
                      }
                    });
                    db.ref("ROOT/Comment/"+key).remove();
              });
            }
            else{
              ref.update({CommentCount:count+1});
            }
          });
          return snap.ref.update({CommentText:data.CommentText,UserId:data.UserId,TimeStamp:new Date().getTime()});
    });

