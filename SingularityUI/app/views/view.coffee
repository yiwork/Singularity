class View extends Backbone.View

    # Keep track of any subviews we may have
    subviews:   {}

    constructor: (params = {}) ->
        @subviews = params.subviews if params.subviews?
        super params

    events: ->
        'click a': 'routeLink'

    remove: ->
        super
        subview.remove() for subview in @subviews

    routeLink: (e) =>
        $link = $(e.target)

        $parentLink = $link.parents('a[href]')
        $link = $parentLink if $parentLink.length

        url = $link.attr('href')

        return true if $link.attr('target') is '_blank' or url is 'javascript:;' or typeof url is 'undefined' or url.indexOf(config.appRoot) != 0

        if e.metaKey or e.ctrlKey or e.shiftKey
            return

        e.preventDefault()

        url = url.replace(config.appRoot, '')

        app.router.navigate url, trigger: true

    afterRender: ->
      Sortable.init()

module.exports = View
