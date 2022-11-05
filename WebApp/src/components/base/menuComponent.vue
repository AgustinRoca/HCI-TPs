<template>
    <v-dialog max-width="290">
        <template v-slot:activator="{ on }">
          <v-btn color="cyan" fab small dark v-on="on">
              <v-icon dark>mdi-pencil</v-icon>
          </v-btn>
        </template>
        <v-card>
            <h2>Hola</h2>
        </v-card>
      </v-dialog>

</template>

<script>
    export default{
        
    }
</script>

