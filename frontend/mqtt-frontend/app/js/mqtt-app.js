
var App = {
  messages      : [],
  subscriptions : [],
};


App.connect = function(args) {
    App.client = new MqttClient(args);

    App.client
        .on('connect', function() {
          console.info('connected to ' + App.client.broker.host + ':' + App.client.broker.port + ' as ' + App.client.broker.clientId);
          App.subscribe({topic:'demo-topic', qos: 1});
        })
        .on('disconnect', function() {
          console.info(App.client.broker.clientId + ' disconnected');
          App.subscriptions = [];
        })
        .on('connecting', console.info.bind(console, 'connecting to ' + App.client.broker.host + ':' + App.client.broker.port))
        .on('offline', console.info.bind(console, App.client.broker.clientId + ' is offline'))
        .on('message', function(topic, payload, message) {
          console.log('got message ' + topic + ' : ' + payload);
        })
        .connect();

    App.host       = App.client.broker.host;
    App.clientId   = App.client.broker.clientId;
    App.disconnect = App.client.disconnect;
    App.subscribe  = function(param) {
        App.client.subscribe(param.topic, param.qos, function(error, granted) {
          if (error) {
            console.error('Error subscribing to ' + param.topic, error);
          } else {
            console.info('subscribed to ' + param.topic + ' with QoS ' + granted);
            App.subscriptions.push({ topic : param.topic, qos : granted });
          }
        });
    };

    App.unsubscribe = function(topic) {
        App.client.unsubscribe(topic, function(error, reply) {
          if (error) {
            console.error('Error unsubscribing from ' + topic, error);
          } else {
            console.info('unsubscribed from ' + topic);
            App.subscriptions = App.subscriptions.filter(function(elem) {
              return elem.topic !== topic;
            });
          }
        });
    };

    App.publish = function(param) {
        App.client.publish(param.topic, param.payload, param, function() { console.log('Published', param); });
    };
}

App.connect({
  host : 'localhost',
  port : 15675,
  path : '/ws',
  will : {
    topic   : 'farewells',
    payload : 'So long!',
  }
});