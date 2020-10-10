def solution(nums):
    return nums

def test(num, input, output):
    try:
        result = solution(input)
        if result == output:
            print("TEST {}|PASSED".format(num))
        else:
            print("TEST {}|FAILED|{}".format(num, result))
    except Exception as e:
        print("TEST {}|ERROR|{}".format(num, str(ex)))

# test case
input1 = [1,2,3,4]
ouput1 = [1,2,3,4]
