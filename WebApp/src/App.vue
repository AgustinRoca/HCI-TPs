<template>
    <v-app>
        <core-app-bar @onFilterChanged="filterChanged"/>

        <core-drawer/>

        <core-view ref="cv"/>

    </v-app>
</template>

<script>
    export default {
        components: {
            CoreDrawer: () => import('@/components/core/Drawer'),
            CoreAppBar: () => import('@/components/core/AppBar'),
            CoreView: () => import('@/components/core/View'),
        },
        methods: {
            filterChanged: function (filter) {
                this.$refs.cv.sendFilter(filter);
            },
        },
    }
</script>
