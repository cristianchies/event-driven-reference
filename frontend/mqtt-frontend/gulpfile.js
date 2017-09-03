var gulp        = require('gulp');
var browserSync = require('browser-sync').create();
var reload      = browserSync.reload;

// Static server
gulp.task('browser-sync', function() {
    browserSync.init({
        server: {
            baseDir: ["app", "bower_components"],
            routes: {
                '/bower_components': 'bower_components/'
            }
        },
        port: 9010
    });

    gulp.watch("*.html").on("change", reload);
});