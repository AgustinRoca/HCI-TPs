<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-app-bar
        id="core-app-bar"
        fixed
        right
        app
        color="smoke"
        elevation=8
        height="60%"
    >
        <v-toolbar-title class="tertiary--text font-weight-light align-self-center">
            <v-btn
                v-if="responsive"
                dark
                icon
                @click.stop="onClick"
            >
                <v-icon>mdi-view-dashboard</v-icon>
            </v-btn>
            {{ title }}
        </v-toolbar-title>

        <v-divider
            class="mx-4"
            vertical
        ></v-divider>

        <v-toolbar-items
            align="left"
            class="flex-grow-1"
        >
            <v-row
                align="center"
                class="mx-0"
            >
                <v-text-field
                    v-model="filter"
                    class="mr-4 flex-grow-1 purple-input"
                    color="purple"
                    label="Search..."
                    width="100%"
                    hide-details
                    v-on:input="filterChanged()"
                ></v-text-field>
            </v-row>
        </v-toolbar-items>

        <div class="flex-grow-0"></div>

        <v-divider
            class="mx-4"
            vertical
        ></v-divider>

        <v-toolbar-items
            align="right"
        >
            <v-row
                align="center"
                class="mx-0"
            >
                <v-btn
                    icon
                    to="/"
                >
                    <v-icon color="tertiary">
                        mdi-home
                    </v-icon>
                </v-btn>

                <v-btn
                    icon
                    to="/Favoritos"
                >
                    <v-icon color="tertiary">
                        mdi-heart
                    </v-icon>
                </v-btn>
            </v-row>
        </v-toolbar-items>
    </v-app-bar>
</template>

<script>
    // Utilities
    import {
        mapMutations,
    } from 'vuex'

    export default {
        data: () => ({
            title: null,
            responsive: false,
            filter: '',
        }),

        watch: {
            '$route' (val) {
                this.title = val.name
            },
        },

        mounted () {
            this.onResponsiveInverted()
            window.addEventListener('resize', this.onResponsiveInverted)
        },
        beforeDestroy () {
            window.removeEventListener('resize', this.onResponsiveInverted)
        },

        methods: {
            ...mapMutations('app', ['setDrawer', 'toggleDrawer']),
            onClick () {
                this.setDrawer(!this.$store.state.app.drawer)
            },
            onResponsiveInverted () {
                if (window.innerWidth < 991) {
                    this.responsive = true
                } else {
                    this.responsive = false
                }
            },
            filterChanged: function(){
                this.$emit("onFilterChanged", this.filter);
            }
        },
    }
</script>

<style>
    /* Fix coming in v2.0.8 */
    #core-app-bar {
        width: auto;
    }

    #core-app-bar a {
        text-decoration: none;
    }
</style>
