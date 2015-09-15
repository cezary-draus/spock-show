import com.blogspot.przybyszd.spock.configuration.Slow

runner {
    if (true) {
        exclude {
            annotation Slow
        }
    }
}