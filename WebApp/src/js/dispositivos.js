export default {
    name: 'Dispositivos',
    dispositivos: [],
    data () {
        return {
            dispositivos: []
        };
    },
    methods: {
        getDispositivos () {
            /*
            fetch('localhost:8080/api/devices', {
                method: 'GET',
                headers: {
                    'content-type': 'application/json; charset=utf-8',
                },
            })
                .then(function (response) {
                    console.log(response)
                    dispositivos = response
                })
                .catch(function (error) {
                    dispositivos = []
                    console.log('Error! Could not reach the API. ' + error)
                })*/
        },
        setDispositivos(dispositivosToSet){
            this.dispositivos = dispositivosToSet.data;
        }
    },
    async created () {
        let devices = await fetch('localhost:8080/api/devices', {
            method: 'GET',
            headers: {
                'content-type': 'application/json; charset=utf-8',
            },
        });
        this.setDispositivos(devices)
        console.log(dispositivos)
    },
}
