package se.sundsvall.byggrarchiver.testutils;

import arendeexport.BatchFilter;
import lombok.AllArgsConstructor;
import org.mockito.ArgumentMatcher;

@AllArgsConstructor
public class BatchFilterMatcher implements ArgumentMatcher<BatchFilter> {

    private BatchFilter left;

    @Override
    public boolean matches(BatchFilter right) {
        if (right == null) {
            return false;
        }
        return left.getLowerExclusiveBound().isEqual(right.getLowerExclusiveBound()) &&
                left.getUpperInclusiveBound().isEqual(right.getUpperInclusiveBound());
    }
}
